package dao.impl;

import dao.ITicketDao;
import dao.generic.AbstractJpaDao;
import entity.Ticket;
import entity.enums.TicketStatus;
import entity.enums.TicketType;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDaoImpl extends AbstractJpaDao<Long, Ticket> implements ITicketDao {

    public TicketDaoImpl() {
        super();
        setClazz(Ticket.class);
    }

    // ============================================================
    // REQUÊTES JPQL
    // ============================================================

    @Override
    public List<Ticket> findByEvent(Long eventId) {
        return entityManager.createQuery(
            "SELECT t FROM Ticket t WHERE t.event.id = :eventId", Ticket.class)
            .setParameter("eventId", eventId)
            .getResultList();
    }

    @Override
    public List<Ticket> findByStatus(TicketStatus status) {
        return entityManager.createQuery(
            "SELECT t FROM Ticket t WHERE t.status = :status", Ticket.class)
            .setParameter("status", status)
            .getResultList();
    }

    @Override
    public List<Ticket> findByOrder(Long orderId) {
        return entityManager.createQuery(
            "SELECT t FROM Ticket t WHERE t.order.id = :orderId", Ticket.class)
            .setParameter("orderId", orderId)
            .getResultList();
    }

    @Override
    public List<Ticket> findByEventAndStatus(Long eventId, TicketStatus status) {
        return entityManager.createQuery(
            "SELECT t FROM Ticket t " +
            "WHERE t.event.id = :eventId " +
            "AND t.status = :status", Ticket.class)
            .setParameter("eventId", eventId)
            .setParameter("status", status)
            .getResultList();
    }

    // ============================================================
    // REQUÊTES NOMMÉES (@NamedQuery)
    // ============================================================

    @Override
    public List<Ticket> findByEventNamed(Long eventId) {
        return entityManager.createNamedQuery("Ticket.findByEvent", Ticket.class)
            .setParameter("eventId", eventId)
            .getResultList();
    }

    @Override
    public List<Ticket> findByStatusNamed(TicketStatus status) {
        return entityManager.createNamedQuery("Ticket.findByStatus", Ticket.class)
            .setParameter("status", status)
            .getResultList();
    }

    @Override
    public Long countByEventAndStatusNamed(Long eventId, TicketStatus status) {
        return entityManager.createNamedQuery("Ticket.countByEventAndStatus", Long.class)
            .setParameter("eventId", eventId)
            .setParameter("status", status)
            .getSingleResult();
    }

    // ============================================================
    // CRITERIA QUERY
    // ============================================================

    @Override
    public List<Ticket> findByCriteria(Long eventId, TicketType type, 
                                       TicketStatus status, Double minPrice, Double maxPrice) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        List<Predicate> predicates = new ArrayList<>();

        if (eventId != null) {
            predicates.add(
                cb.equal(root.get("event").get("id"), eventId)
            );
        }

        if (type != null) {
            predicates.add(
                cb.equal(root.get("ticketType"), type)
            );
        }

        if (status != null) {
            predicates.add(
                cb.equal(root.get("status"), status)
            );
        }

        if (minPrice != null) {
            predicates.add(
                cb.greaterThanOrEqualTo(root.get("price"), minPrice)
            );
        }

        if (maxPrice != null) {
            predicates.add(
                cb.lessThanOrEqualTo(root.get("price"), maxPrice)
            );
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("price")));

        return entityManager.createQuery(cq).getResultList();
    }

    // ============================================================
    // MÉTHODES MÉTIER
    // ============================================================

    @Override
    public Optional<Ticket> findByQrCode(String qrCode) {
        return entityManager.createQuery(
            "SELECT t FROM Ticket t WHERE t.qrCode = :qrCode", Ticket.class)
            .setParameter("qrCode", qrCode)
            .getResultStream()
            .findFirst();
    }

    @Override
    public Long countSoldTicketsByEvent(Long eventId) {
        return entityManager.createQuery(
            "SELECT COUNT(t) FROM Ticket t " +
            "WHERE t.event.id = :eventId " +
            "AND t.status = 'SOLD'", Long.class)
            .setParameter("eventId", eventId)
            .getSingleResult();
    }

    @Override
    public Double getRevenuByEvent(Long eventId) {
        Double revenue = entityManager.createQuery(
            "SELECT SUM(t.price) FROM Ticket t " +
            "WHERE t.event.id = :eventId " +
            "AND t.status = 'SOLD'", Double.class)
            .setParameter("eventId", eventId)
            .getSingleResult();
        return revenue != null ? revenue : 0.0;
    }

    @Override
    public List<Ticket> findAvailableTicketsByEvent(Long eventId) {
        return entityManager.createQuery(
            "SELECT t FROM Ticket t " +
            "WHERE t.event.id = :eventId " +
            "AND t.status = 'AVAILABLE' " +
            "ORDER BY t.ticketType ASC, t.price ASC", Ticket.class)
            .setParameter("eventId", eventId)
            .getResultList();
    }
}