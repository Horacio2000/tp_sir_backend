package dao.impl;

import dao.IEventDao;
import dao.generic.AbstractJpaDao;
import entity.Event;
import entity.enums.EventStatus;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventDaoImpl extends AbstractJpaDao<Long, Event> implements IEventDao {

    public EventDaoImpl() {
        super();
        setClazz(Event.class);
    }

    // ============================================================
    // REQUÊTES JPQL
    // ============================================================

    @Override
    public List<Event> findByStatus(EventStatus status) {
        return entityManager.createQuery(
            "SELECT e FROM Event e WHERE e.status = :status", Event.class)
            .setParameter("status", status)
            .getResultList();
    }

    @Override
    public List<Event> findByCity(String city) {
        return entityManager.createQuery(
            "SELECT e FROM Event e WHERE e.venue.city = :city", Event.class)
            .setParameter("city", city)
            .getResultList();
    }

    @Override
    public List<Event> findAvailableEvents() {
        return entityManager.createQuery(
            "SELECT e FROM Event e " +
            "WHERE e.availableTickets > 0 " +
            "AND e.status = :status " +
            "AND e.eventDate >= CURRENT_DATE " +
            "ORDER BY e.eventDate ASC", Event.class)
            .setParameter("status", EventStatus.PUBLISHED)
            .getResultList();
    }

    @Override
    public List<Event> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return entityManager.createQuery(
            "SELECT e FROM Event e " +
            "WHERE e.eventDate BETWEEN :startDate AND :endDate " +
            "ORDER BY e.eventDate ASC", Event.class)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();
    }

    // ============================================================
    // REQUÊTES NOMMÉES (@NamedQuery)
    // ============================================================

    @Override
    public List<Event> findByOrganizer(Long organizerId) {
        return entityManager.createNamedQuery("Event.findByOrganizer", Event.class)
            .setParameter("organizerId", organizerId)
            .getResultList();
    }

    @Override
    public List<Event> findUpcomingEvents() {
        return entityManager.createNamedQuery("Event.findUpcoming", Event.class)
            .getResultList();
    }

    // ============================================================
    // CRITERIA QUERY
    // ============================================================

    @Override
    public List<Event> findByCriteria(String city, EventStatus status, 
                                      Double maxPrice, LocalDate fromDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> root = cq.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (city != null && !city.isEmpty()) {
            predicates.add(
                cb.equal(root.join("venue").get("city"), city)
            );
        }

        if (status != null) {
            predicates.add(
                cb.equal(root.get("status"), status)
            );
        }

        if (maxPrice != null) {
            predicates.add(
                cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice)
            );
        }

        if (fromDate != null) {
            predicates.add(
                cb.greaterThanOrEqualTo(root.get("eventDate"), fromDate)
            );
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("eventDate")));

        return entityManager.createQuery(cq).getResultList();
    }

    // ============================================================
    // MÉTHODES MÉTIER
    // ============================================================

    @Override
    public Long countTicketsSold(Long eventId) {
        return entityManager.createQuery(
            "SELECT COUNT(t) FROM Ticket t " +
            "WHERE t.event.id = :eventId " +
            "AND t.status = 'SOLD'", Long.class)
            .setParameter("eventId", eventId)
            .getSingleResult();
    }

    @Override
    public Double calculateRevenue(Long eventId) {
        Double revenue = entityManager.createQuery(
            "SELECT SUM(t.price) FROM Ticket t " +
            "WHERE t.event.id = :eventId " +
            "AND t.status = 'SOLD'", Double.class)
            .setParameter("eventId", eventId)
            .getSingleResult();
        return revenue != null ? revenue : 0.0;
    }

    @Override
    public List<Event> findPopularEvents(int limit) {
        return entityManager.createQuery(
            "SELECT e FROM Event e " +
            "WHERE e.status = 'PUBLISHED' " +
            "ORDER BY (e.totalTickets - e.availableTickets) DESC", Event.class)
            .setMaxResults(limit)
            .getResultList();
    }

    @Override
    public void updateAvailableTickets(Long eventId, int delta) {
        EntityTransaction t = entityManager.getTransaction();
        t.begin();
        String jpql = delta < 0
            ? "UPDATE Event e SET e.availableTickets = e.availableTickets - :count WHERE e.id = :id"
            : "UPDATE Event e SET e.availableTickets = e.availableTickets + :count WHERE e.id = :id";
        entityManager.createQuery(jpql)
            .setParameter("count", Math.abs(delta))
            .setParameter("id", eventId)
            .executeUpdate();
        t.commit();
    }
}