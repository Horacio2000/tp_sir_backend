package dao;

import dao.generic.IGenericDao;
import entity.Ticket;
import entity.enums.TicketStatus;
import entity.enums.TicketType;
import java.util.List;
import java.util.Optional;

public interface ITicketDao extends IGenericDao<Long, Ticket> {
    
    // JPQL
    List<Ticket> findByEvent(Long eventId);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByOrder(Long orderId);
    List<Ticket> findByEventAndStatus(Long eventId, TicketStatus status);
    
    // Named Query
    List<Ticket> findByEventNamed(Long eventId);
    List<Ticket> findByStatusNamed(TicketStatus status);
    Long countByEventAndStatusNamed(Long eventId, TicketStatus status);
    
    // Criteria Query
    List<Ticket> findByCriteria(Long eventId, TicketType type, TicketStatus status, Double minPrice, Double maxPrice);
    
    // Méthodes métier
    Optional<Ticket> findByQrCode(String qrCode);
    Long countSoldTicketsByEvent(Long eventId);
    Double getRevenuByEvent(Long eventId);
    List<Ticket> findAvailableTicketsByEvent(Long eventId);
}