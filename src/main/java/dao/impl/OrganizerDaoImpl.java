package dao.impl;

import dao.IOrganizerDao;
import dao.generic.AbstractJpaDao;
import entity.user.Organizer;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganizerDaoImpl extends AbstractJpaDao<Long, Organizer> 
    implements IOrganizerDao {

    public OrganizerDaoImpl() {
        super();
        setClazz(Organizer.class);
    }

    // ============================================================
    // REQUÊTES JPQL
    // ============================================================

    @Override
    public Optional<Organizer> findByEmail(String email) {
        return entityManager.createQuery(
            "SELECT o FROM Organizer o WHERE o.email = :email", Organizer.class)
            .setParameter("email", email)
            .getResultStream()
            .findFirst();
    }

    @Override
    public Optional<Organizer> findBySiret(String siret) {
        return entityManager.createQuery(
            "SELECT o FROM Organizer o WHERE o.siret = :siret", Organizer.class)
            .setParameter("siret", siret)
            .getResultStream()
            .findFirst();
    }

    @Override
    public List<Organizer> findByCompanyName(String companyName) {
        return entityManager.createQuery(
            "SELECT o FROM Organizer o " +
            "WHERE LOWER(o.companyName) LIKE LOWER(:name)", Organizer.class)
            .setParameter("name", "%" + companyName + "%")
            .getResultList();
    }

    // ============================================================
    // CRITERIA QUERY
    // ============================================================

    @Override
    public List<Organizer> findByCriteria(String companyName, String city) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Organizer> cq = cb.createQuery(Organizer.class);
        Root<Organizer> root = cq.from(Organizer.class);

        List<Predicate> predicates = new ArrayList<>();

        if (companyName != null && !companyName.isEmpty()) {
            predicates.add(cb.like(
                cb.lower(root.get("companyName")),
                "%" + companyName.toLowerCase() + "%"
            ));
        }

        // Note: pour filtrer par ville, il faudrait joindre avec Event puis Venue
        if (city != null && !city.isEmpty()) {
            Join<Object, Object> eventJoin = root.join("events", JoinType.LEFT);
            Join<Object, Object> venueJoin = eventJoin.join("venue", JoinType.LEFT);
            predicates.add(cb.equal(
                cb.lower(venueJoin.get("city")),
                city.toLowerCase()
            ));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.distinct(true);

        return entityManager.createQuery(cq).getResultList();
    }

    // ============================================================
    // MÉTHODES MÉTIER
    // ============================================================

    @Override
    public int getEventCount(Long organizerId) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(e) FROM Event e WHERE e.organizer.id = :organizerId", Long.class)
            .setParameter("organizerId", organizerId)
            .getSingleResult();
        return count.intValue();
    }

    @Override
    public Double getTotalRevenue(Long organizerId) {
        Double revenue = entityManager.createQuery(
            "SELECT SUM(t.price) FROM Ticket t " +
            "WHERE t.event.organizer.id = :organizerId " +
            "AND t.status = 'SOLD'", Double.class)
            .setParameter("organizerId", organizerId)
            .getSingleResult();
        return revenue != null ? revenue : 0.0;
    }

    @Override
    public List<Organizer> findTopOrganizers(int limit) {
        return entityManager.createQuery(
            "SELECT o FROM Organizer o " +
            "ORDER BY SIZE(o.events) DESC", Organizer.class)
            .setMaxResults(limit)
            .getResultList();
    }
}