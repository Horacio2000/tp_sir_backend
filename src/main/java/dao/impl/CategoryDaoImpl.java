package dao.impl;

import dao.ICategoryDao;
import dao.generic.AbstractJpaDao;
import entity.Category;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDaoImpl extends AbstractJpaDao<Long, Category> 
    implements ICategoryDao {

    public CategoryDaoImpl() {
        super();
        setClazz(Category.class);
    }

    // ============================================================
    // REQUÊTES JPQL
    // ============================================================

    @Override
    public Optional<Category> findByName(String name) {
        return entityManager.createQuery(
            "SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name)", Category.class)
            .setParameter("name", name)
            .getResultStream()
            .findFirst();
    }

    @Override
    public List<Category> findByNameContaining(String keyword) {
        return entityManager.createQuery(
            "SELECT c FROM Category c " +
            "WHERE LOWER(c.name) LIKE LOWER(:keyword) " +
            "OR LOWER(c.description) LIKE LOWER(:keyword)", Category.class)
            .setParameter("keyword", "%" + keyword + "%")
            .getResultList();
    }

    // ============================================================
    // REQUÊTES NOMMÉES (@NamedQuery)
    // ============================================================

    @Override
    public Optional<Category> findByNameNamed(String name) {
        return entityManager.createNamedQuery("Category.findByName", Category.class)
            .setParameter("name", "%" + name + "%")
            .getResultStream()
            .findFirst();
    }

    @Override
    public List<Category> findPopularCategories() {
        return entityManager.createNamedQuery("Category.findPopular", Category.class)
            .setMaxResults(10)
            .getResultList();
    }

    // ============================================================
    // CRITERIA QUERY
    // ============================================================

    @Override
    public List<Category> findByCriteria(String name, boolean hasEvents) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Root<Category> root = cq.from(Category.class);

        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(
                cb.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
            ));
        }

        if (hasEvents) {
            predicates.add(
                cb.greaterThan(cb.size(root.get("events")), 0)
            );
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("name")));

        return entityManager.createQuery(cq).getResultList();
    }

    // ============================================================
    // MÉTHODES MÉTIER
    // ============================================================

    @Override
    public int getEventCount(Long categoryId) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(e) FROM Event e JOIN e.categories c " +
            "WHERE c.id = :categoryId", Long.class)
            .setParameter("categoryId", categoryId)
            .getSingleResult();
        return count.intValue();
    }

    @Override
    public List<Category> findCategoriesWithMinEvents(int minEvents) {
        return entityManager.createQuery(
            "SELECT c FROM Category c " +
            "WHERE SIZE(c.events) >= :minEvents " +
            "ORDER BY SIZE(c.events) DESC", Category.class)
            .setParameter("minEvents", minEvents)
            .getResultList();
    }
}