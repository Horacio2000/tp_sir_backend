package dao;

import dao.generic.IGenericDao;
import entity.user.Client;
import java.util.List;
import java.util.Optional;

public interface IClientDao extends IGenericDao<Long, Client> {
    
    // JPQL
    Optional<Client> findByEmail(String email);
    List<Client> findByLoyaltyPointsGreaterThan(int points);
    
    // Named Query
    List<Client> findTopClients();
    List<Client> findByMinLoyaltyPoints(int points);
    
    // Criteria Query
    List<Client> findByCriteria(String firstName, String lastName, String email);
    
    // Méthodes métier
    void addLoyaltyPoints(Long clientId, int points);
    int getTotalOrdersCount(Long clientId);
    Double getTotalSpent(Long clientId);
    List<Client> findClientsWithoutOrders();
}