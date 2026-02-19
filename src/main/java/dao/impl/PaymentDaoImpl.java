package dao.impl;

import dao.IPaymentDao;
import dao.generic.AbstractJpaDao;
import entity.Payment;
import entity.enums.PaymentMethod;
import entity.enums.PaymentStatus;
import jakarta.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentDaoImpl extends AbstractJpaDao<Long, Payment> implements IPaymentDao {

    public PaymentDaoImpl() {
        super();
        setClazz(Payment.class);
    }

    // ============================================================
    // REQUÊTES JPQL
    // ============================================================

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return entityManager.createQuery(
            "SELECT p FROM Payment p WHERE p.status = :status", Payment.class)
            .setParameter("status", status)
            .getResultList();
    }

    @Override
    public List<Payment> findByMethod(PaymentMethod method) {
        return entityManager.createQuery(
            "SELECT p FROM Payment p WHERE p.method = :method", Payment.class)
            .setParameter("method", method)
            .getResultList();
    }

    @Override
    public List<Payment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return entityManager.createQuery(
            "SELECT p FROM Payment p " +
            "WHERE p.paymentDate BETWEEN :startDate AND :endDate " +
            "ORDER BY p.paymentDate DESC", Payment.class)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();
    }

    @Override
    public Optional<Payment> findByTransactionId(String transactionId) {
        return entityManager.createQuery(
            "SELECT p FROM Payment p WHERE p.transactionId = :transactionId", Payment.class)
            .setParameter("transactionId", transactionId)
            .getResultStream()
            .findFirst();
    }

    // ============================================================
    // CRITERIA QUERY
    // ============================================================

    @Override
    public List<Payment> findByCriteria(PaymentStatus status, PaymentMethod method, 
                                        Double minAmount, Double maxAmount) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Payment> cq = cb.createQuery(Payment.class);
        Root<Payment> root = cq.from(Payment.class);

        List<Predicate> predicates = new ArrayList<>();

        if (status != null) {
            predicates.add(
                cb.equal(root.get("status"), status)
            );
        }

        if (method != null) {
            predicates.add(
                cb.equal(root.get("method"), method)
            );
        }

        if (minAmount != null) {
            predicates.add(
                cb.greaterThanOrEqualTo(root.get("amount"), minAmount)
            );
        }

        if (maxAmount != null) {
            predicates.add(
                cb.lessThanOrEqualTo(root.get("amount"), maxAmount)
            );
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get("paymentDate")));

        return entityManager.createQuery(cq).getResultList();
    }

    // ============================================================
    // MÉTHODES MÉTIER
    // ============================================================

    @Override
    public Double getTotalAmount() {
        Double total = entityManager.createQuery(
            "SELECT SUM(p.amount) FROM Payment p " +
            "WHERE p.status = 'COMPLETED'", Double.class)
            .getSingleResult();
        return total != null ? total : 0.0;
    }

    @Override
    public Double getTotalAmountByStatus(PaymentStatus status) {
        Double total = entityManager.createQuery(
            "SELECT SUM(p.amount) FROM Payment p " +
            "WHERE p.status = :status", Double.class)
            .setParameter("status", status)
            .getSingleResult();
        return total != null ? total : 0.0;
    }

    @Override
    public Double getTotalAmountByMethod(PaymentMethod method) {
        Double total = entityManager.createQuery(
            "SELECT SUM(p.amount) FROM Payment p " +
            "WHERE p.method = :method " +
            "AND p.status = 'COMPLETED'", Double.class)
            .setParameter("method", method)
            .getSingleResult();
        return total != null ? total : 0.0;
    }

    @Override
    public Long countByStatus(PaymentStatus status) {
        return entityManager.createQuery(
            "SELECT COUNT(p) FROM Payment p WHERE p.status = :status", Long.class)
            .setParameter("status", status)
            .getSingleResult();
    }
}