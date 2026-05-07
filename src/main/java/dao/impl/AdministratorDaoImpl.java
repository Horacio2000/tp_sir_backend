package dao.impl;

import dao.IAdministratorDao;
import dao.generic.AbstractJpaDao;
import entity.user.Administrator;
import java.util.Optional;

public class AdministratorDaoImpl extends AbstractJpaDao<Long, Administrator>
        implements IAdministratorDao {

    public AdministratorDaoImpl() {
        super();
        setClazz(Administrator.class);
    }

    @Override
    public Optional<Administrator> findByEmail(String email) {
        return entityManager.createQuery(
                "SELECT a FROM Administrator a WHERE a.email = :email", Administrator.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }
}
