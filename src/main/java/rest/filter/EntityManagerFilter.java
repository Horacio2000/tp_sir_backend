package rest.filter;

import dao.generic.EntityManagerHelper;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class EntityManagerFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) throws IOException {
        // Rollback si une transaction a été laissée ouverte par erreur
        try {
            var tx = EntityManagerHelper.getEntityManager().getTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
        } catch (Exception ignored) {}

        EntityManagerHelper.closeEntityManager();
    }
}
