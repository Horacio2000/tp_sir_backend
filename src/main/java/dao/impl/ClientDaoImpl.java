package dao.impl;

import dao.IClientDao;
import dao.generic.AbstractJpaDao;
import entity.user.Client;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDaoImpl extends AbstractJpaDao<Long, Client> implements IClientDao {

    public ClientDaoImpl() {
        super();
        setClazz(Client.class);
    }

    // ============================================================
    // REQUÊTES JPQL
    // ============================================================

    @Override
    public Optional<Client> findByEmail(String email) {
        return entityManager.createQuery(
            "SELECT c FROM Client c WHERE c.email = :email", Client.class)
            .setParameter("email", email)
            .getResultStream()
            .findFirst();
    }

    @Override
    public List<Client> findByLoyaltyPointsGreaterThan(int points) {
        return entityManager.createQuery(
            "SELECT c FROM Client c WHERE c.loyaltyPoints > :points " +
            "ORDER BY c.loyaltyPoints DESC", Client.class)
            .setParameter("points", points)
            .getResultList();
    }

    // ============================================================
    // REQUÊTES NOMMÉES (@NamedQuery)
    // ============================================================

    @Override
    public List<Client> findTopClients() {
        return entityManager.createNamedQuery("Client.findTopByLoyaltyPoints", Client.class)
            .setMaxResults(10)
            .getResultList();
    }

    @Override
    public List<Client> findByMinLoyaltyPoints(int points) {
        return entityManager.createNamedQuery("Client.findByMinLoyaltyPoints", Client.class)
            .setParameter("points", points)
            .getResultList();
    }

    // ============================================================
    // CRITERIA QUERY
    // ============================================================

    @Override
    public List<Client> findByCriteria(String firstName, String lastName, String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> root = cq.from(Client.class);

        List<Predicate> predicates = new ArrayList<>();

        if (firstName != null && !firstName.isEmpty()) {
            predicates.add(cb.like(
                cb.lower(root.get("firstName")),
                "%" + firstName.toLowerCase() + "%"
            ));
        }

        if (lastName != null && !lastName.isEmpty()) {
            predicates.add(cb.like(
                cb.lower(root.get("lastName")),
                "%" + lastName.toLowerCase() + "%"
            ));
        }

        if (email != null && !email.isEmpty()) {
            predicates.add(cb.like(
                cb.lower(root.get("email")),
                "%" + email.toLowerCase() + "%"
            ));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("lastName")), cb.asc(root.get("firstName")));

        return entityManager.createQuery(cq).getResultList();
    }

    // ============================================================
    // MÉTHODES MÉTIER
    // ============================================================

    @Override
    public void addLoyaltyPoints(Long clientId, int points) {
        EntityTransaction t = entityManager.getTransaction();
        t.begin();
        entityManager.createQuery(
            "UPDATE Client c SET c.loyaltyPoints = c.loyaltyPoints + :points WHERE c.id = :id")
            .setParameter("points", points)
            .setParameter("id", clientId)
            .executeUpdate();
        t.commit();
    }


    @Override
    public int getTotalOrdersCount(Long clientId) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.client.id = :clientId", Long.class)
            .setParameter("clientId", clientId)
            .getSingleResult();
        return count.intValue();
    }

    @Override
    public Double getTotalSpent(Long clientId) {
        Double total = entityManager.createQuery(
            "SELECT SUM(o.totalAmount) FROM Order o " +
            "WHERE o.client.id = :clientId " +
            "AND o.status = 'CONFIRMED'", Double.class)
            .setParameter("clientId", clientId)
            .getSingleResult();
        return total != null ? total : 0.0;
    }

    @Override
    public List<Client> findClientsWithoutOrders() {
        return entityManager.createQuery(
            "SELECT c FROM Client c WHERE SIZE(c.orders) = 0", Client.class)
            .getResultList();
    }
}