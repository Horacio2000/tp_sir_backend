package dao;

import dao.generic.IGenericDao;
import entity.Venue;
import java.util.List;

public interface IVenueDao extends IGenericDao<Long, Venue> {
    
    // JPQL
    List<Venue> findByCity(String city);
    List<Venue> findByCountry(String country);
    List<Venue> findByCapacityGreaterThan(int capacity);
    
    // Named Query
    List<Venue> findByCityNamed(String city);
    List<Venue> findByMinCapacityNamed(int minCapacity);
    
    // Criteria Query
    List<Venue> findByCriteria(String city, String country, Integer minCapacity, Integer maxCapacity);
    
    // Méthodes métier
    int getEventCount(Long venueId);
    List<Venue> findMostUsedVenues(int limit);
}