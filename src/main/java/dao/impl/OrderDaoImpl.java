package dao.impl;

import dao.IOrderDao;
import dao.generic.AbstractJpaDao;
import entity.Order;
import entity.enums.OrderStatus;
import jakarta.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl extends AbstractJpaDao<Long, Order> implements IOrderDao {

    public OrderDaoImpl() {
        super();
        setClazz(Order.class);
    }

    // ============================================================
    // REQUÊTES JPQL
    // ============================================================

    @Override
    public List<Order> findByClient(Long clientId) {
        return entityManager.createQuery(
            "SELECT o FROM Order o " +
            "WHERE o.client.id = :clientId " +
            "ORDER BY o.orderDate DESC", Order.class)
            .setParameter("clientId", clientId)
            .getResultList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.status = :status", Order.class)
            .setParameter("status", status)
            .getResultList();
    }

    @Override
    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return entityManager.createQuery(
            "SELECT o FROM Order o " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
            "ORDER BY o.orderDate DESC", Order.class)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();
    }

    // ============================================================
    // REQUÊTES NOMMÉES (@NamedQuery)
    // ============================================================

    @Override
    public List<Order> findByClientNamed(Long clientId) {
        return entityManager.createNamedQuery("Order.findByClient", Order.class)
            .setParameter("clientId", clientId)
            .getResultList();
    }

    @Override
    public List<Order> findByStatusNamed(OrderStatus status) {
        return entityManager.createNamedQuery("Order.findByStatus", Order.class)
            .setParameter("status", status)
            .getResultList();
    }

    @Override
    public List<Order> findRecentOrders(LocalDateTime date) {
        return entityManager.createNamedQuery("Order.findRecentOrders", Order.class)
            .setParameter("date", date)
            .getResultList();
    }

    // ============================================================
    // CRITERIA QUERY
    // ============================================================

    @Override
    public List<Order> findByCriteria(Long clientId, OrderStatus status, 
                                      Double minAmount, Double maxAmount) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();

        if (clientId != null) {
            predicates.add(
                cb.equal(root.get("client").get("id"), clientId)
            );
        }

        if (status != null) {
            predicates.add(
                cb.equal(root.get("status"), status)
            );
        }

        if (minAmount != null) {
            predicates.add(
                cb.greaterThanOrEqualTo(root.get("totalAmount"), minAmount)
            );
        }

        if (maxAmount != null) {
            predicates.add(
                cb.lessThanOrEqualTo(root.get("totalAmount"), maxAmount)
            );
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get("orderDate")));

        return entityManager.createQuery(cq).getResultList();
    }

    // ============================================================
    // MÉTHODES MÉTIER
    // ============================================================

    @Override
    public Double getTotalRevenue() {
        Double revenue = entityManager.createQuery(
            "SELECT SUM(o.totalAmount) FROM Order o " +
            "WHERE o.status = 'CONFIRMED'", Double.class)
            .getSingleResult();
        return revenue != null ? revenue : 0.0;
    }

    @Override
    public Double getTotalRevenueByClient(Long clientId) {
        Double revenue = entityManager.createQuery(
            "SELECT SUM(o.totalAmount) FROM Order o " +
            "WHERE o.client.id = :clientId " +
            "AND o.status = 'CONFIRMED'", Double.class)
            .setParameter("clientId", clientId)
            .getSingleResult();
        return revenue != null ? revenue : 0.0;
    }

    @Override
    public Long countOrdersByStatus(OrderStatus status) {
        return entityManager.createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.status = :status", Long.class)
            .setParameter("status", status)
            .getSingleResult();
    }

    @Override
    public List<Order> findTopOrders(int limit) {
        return entityManager.createQuery(
            "SELECT o FROM Order o " +
            "WHERE o.status = 'CONFIRMED' " +
            "ORDER BY o.totalAmount DESC", Order.class)
            .setMaxResults(limit)
            .getResultList();
    }
}