package dao;

import dao.generic.IGenericDao;
import entity.Event;
import entity.enums.EventStatus;
import java.time.LocalDate;
import java.util.List;

public interface IEventDao extends IGenericDao<Long, Event> {
    
    // JPQL
    List<Event> findByStatus(EventStatus status);
    List<Event> findByCity(String city);
    List<Event> findAvailableEvents();
    List<Event> findByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Named Query
    List<Event> findByOrganizer(Long organizerId);
    List<Event> findUpcomingEvents();
    
    // Criteria Query
    List<Event> findByCriteria(String city, EventStatus status, Double maxPrice, LocalDate fromDate);
    
    // Méthodes métier
    Long countTicketsSold(Long eventId);
    Double calculateRevenue(Long eventId);
    List<Event> findPopularEvents(int limit);
    void updateAvailableTickets(Long eventId, int delta);
}