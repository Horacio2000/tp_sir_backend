package dao;

import dao.generic.IGenericDao;
import entity.Category;
import java.util.List;
import java.util.Optional;

public interface ICategoryDao extends IGenericDao<Long, Category> {
    
    // JPQL
    Optional<Category> findByName(String name);
    List<Category> findByNameContaining(String keyword);
    
    // Named Query
    Optional<Category> findByNameNamed(String name);
    List<Category> findPopularCategories();
    
    // Criteria Query
    List<Category> findByCriteria(String name, boolean hasEvents);
    
    // Méthodes métier
    int getEventCount(Long categoryId);
    List<Category> findCategoriesWithMinEvents(int minEvents);
}