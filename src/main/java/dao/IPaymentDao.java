package dao;

import dao.generic.IGenericDao;
import entity.Payment;
import entity.enums.PaymentMethod;
import entity.enums.PaymentStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IPaymentDao extends IGenericDao<Long, Payment> {
    
    // JPQL
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByMethod(PaymentMethod method);
    List<Payment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    Optional<Payment> findByTransactionId(String transactionId);
    
    // Named Query (si besoin)
    
    // Criteria Query
    List<Payment> findByCriteria(PaymentStatus status, PaymentMethod method, Double minAmount, Double maxAmount);
    
    // Méthodes métier
    Double getTotalAmount();
    Double getTotalAmountByStatus(PaymentStatus status);
    Double getTotalAmountByMethod(PaymentMethod method);
    Long countByStatus(PaymentStatus status);
}