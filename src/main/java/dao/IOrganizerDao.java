package dao;

import dao.generic.IGenericDao;
import entity.user.Organizer;
import java.util.List;
import java.util.Optional;

public interface IOrganizerDao extends IGenericDao<Long, Organizer> {
    
    // JPQL
    Optional<Organizer> findByEmail(String email);
    Optional<Organizer> findBySiret(String siret);
    List<Organizer> findByCompanyName(String companyName);
    
    // Named Query (on peut ajouter si besoin)
    
    // Criteria Query
    List<Organizer> findByCriteria(String companyName, String city);
    
    // Méthodes métier
    int getEventCount(Long organizerId);
    Double getTotalRevenue(Long organizerId);
    List<Organizer> findTopOrganizers(int limit);
}