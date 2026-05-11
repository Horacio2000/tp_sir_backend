package servlet;

import dao.IAdministratorDao;
import dao.impl.AdministratorDaoImpl;
import dao.generic.EntityManagerHelper;
import entity.user.Administrator;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DataInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        IAdministratorDao adminDao = new AdministratorDaoImpl();

        // Créer l'admin uniquement s'il n'existe pas déjà
        adminDao.findByEmail("admin@concert.fr").ifPresentOrElse(
            existing -> System.out.println("[DataInitializer] Admin déjà présent : " + existing.getEmail()),
            () -> {
                EntityManagerHelper.beginTransaction();
                Administrator admin = new Administrator(
                    "admin@concert.fr",
                    "admin123",
                    "Super",
                    "Admin"
                );
                admin.setAccessLevel("FULL");
                admin.setDepartment("Direction");
                adminDao.save(admin);
                EntityManagerHelper.commit();
                System.out.println("[DataInitializer] Compte admin créé : admin@concert.fr / admin123");
            }
        );
    }
}
