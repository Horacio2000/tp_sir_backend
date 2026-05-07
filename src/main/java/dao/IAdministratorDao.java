package dao;

import dao.generic.IGenericDao;
import entity.user.Administrator;
import java.util.Optional;

public interface IAdministratorDao extends IGenericDao<Long, Administrator> {
    Optional<Administrator> findByEmail(String email);
}
