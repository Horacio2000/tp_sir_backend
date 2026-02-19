package dao;

import dao.generic.IGenericDao;
import entity.Order;
import entity.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface IOrderDao extends IGenericDao<Long, Order> {
    
    // JPQL
    List<Order> findByClient(Long clientId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    // Named Query
    List<Order> findByClientNamed(Long clientId);
    List<Order> findByStatusNamed(OrderStatus status);
    List<Order> findRecentOrders(LocalDateTime date);
    
    // Criteria Query
    List<Order> findByCriteria(Long clientId, OrderStatus status, Double minAmount, Double maxAmount);
    
    // Méthodes métier
    Double getTotalRevenue();
    Double getTotalRevenueByClient(Long clientId);
    Long countOrdersByStatus(OrderStatus status);
    List<Order> findTopOrders(int limit);
}