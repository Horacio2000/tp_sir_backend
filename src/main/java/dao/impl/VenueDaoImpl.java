package dao.impl;

import dao.IVenueDao;
import dao.generic.AbstractJpaDao;
import entity.Venue;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class VenueDaoImpl extends AbstractJpaDao<Long, Venue> implements IVenueDao {

    public VenueDaoImpl() {
        super();
        setClazz(Venue.class);
    }

    // ============================================================
    // REQUÊTES JPQL
    // ============================================================

    @Override
    public List<Venue> findByCity(String city) {
        return entityManager.createQuery(
            "SELECT v FROM Venue v WHERE LOWER(v.city) = LOWER(:city)", Venue.class)
            .setParameter("city", city)
            .getResultList();
    }

    @Override
    public List<Venue> findByCountry(String country) {
        return entityManager.createQuery(
            "SELECT v FROM Venue v WHERE LOWER(v.country) = LOWER(:country)", Venue.class)
            .setParameter("country", country)
            .getResultList();
    }

    @Override
    public List<Venue> findByCapacityGreaterThan(int capacity) {
        return entityManager.createQuery(
            "SELECT v FROM Venue v " +
            "WHERE v.capacity > :capacity " +
            "ORDER BY v.capacity ASC", Venue.class)
            .setParameter("capacity", capacity)
            .getResultList();
    }

    // ============================================================
    // REQUÊTES NOMMÉES (@NamedQuery)
    // ============================================================

    @Override
    public List<Venue> findByCityNamed(String city) {
        return entityManager.createNamedQuery("Venue.findByCity", Venue.class)
            .setParameter("city", city)
            .getResultList();
    }

    @Override
    public List<Venue> findByMinCapacityNamed(int minCapacity) {
        return entityManager.createNamedQuery("Venue.findByMinCapacity", Venue.class)
            .setParameter("minCapacity", minCapacity)
            .getResultList();
    }

    // ============================================================
    // CRITERIA QUERY
    // ============================================================

    @Override
    public List<Venue> findByCriteria(String city, String country, 
                                      Integer minCapacity, Integer maxCapacity) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Venue> cq = cb.createQuery(Venue.class);
        Root<Venue> root = cq.from(Venue.class);

        List<Predicate> predicates = new ArrayList<>();

        if (city != null && !city.isEmpty()) {
            predicates.add(cb.equal(
                cb.lower(root.get("city")),
                city.toLowerCase()
            ));
        }

        if (country != null && !country.isEmpty()) {
            predicates.add(cb.equal(
                cb.lower(root.get("country")),
                country.toLowerCase()
            ));
        }

        if (minCapacity != null) {
            predicates.add(
                cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity)
            );
        }

        if (maxCapacity != null) {
            predicates.add(
                cb.lessThanOrEqualTo(root.get("capacity"), maxCapacity)
            );
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get("capacity")));

        return entityManager.createQuery(cq).getResultList();
    }

    // ============================================================
    // MÉTHODES MÉTIER
    // ============================================================

    @Override
    public int getEventCount(Long venueId) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(e) FROM Event e WHERE e.venue.id = :venueId", Long.class)
            .setParameter("venueId", venueId)
            .getSingleResult();
        return count.intValue();
    }

    @Override
    public List<Venue> findMostUsedVenues(int limit) {
        return entityManager.createQuery(
            "SELECT v FROM Venue v " +
            "ORDER BY SIZE(v.events) DESC", Venue.class)
            .setMaxResults(limit)
            .getResultList();
    }
}