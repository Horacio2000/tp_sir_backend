package dao.generic;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public abstract class AbstractJpaDao<K, T extends Serializable> implements IGenericDao<K, T> {

	private Class<T> clazz;

	protected EntityManager entityManager;

	public AbstractJpaDao() {
		this.entityManager = EntityManagerHelper.getEntityManager();
	}

	public void setClazz(Class<T> clazzToSet) {
		this.clazz = clazzToSet;
	}

	public T findOne(K id) {
		return entityManager.find(clazz, id);
	}

	public List<T> findAll() {
		return entityManager.createQuery("select e from " + clazz.getName() + " as e",clazz).getResultList();
	}

	public void save(T entity) {
		EntityTransaction t = this.entityManager.getTransaction();
		boolean started = !t.isActive();
		if (started) t.begin();
		entityManager.persist(entity);
		if (started) t.commit();
	}

	public T update(final T entity) {
		EntityTransaction t = this.entityManager.getTransaction();
		boolean started = !t.isActive();
		if (started) t.begin();
		T res = entityManager.merge(entity);
		if (started) t.commit();
		return res;
	}

	public void delete(T entity) {
		EntityTransaction t = this.entityManager.getTransaction();
		boolean started = !t.isActive();
		if (started) t.begin();
		entityManager.remove(entity);
		if (started) t.commit();
	}


	public void deleteById(K entityId) {
		T entity = findOne(entityId);
		delete(entity);
	}
}
