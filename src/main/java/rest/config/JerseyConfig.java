package rest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import java.util.HashSet;
import java.util.Set;

/**
 * Configuration de l'application REST Jersey avec Swagger/OpenAPI
 */
@ApplicationPath("/api")
@OpenAPIDefinition(
    info = @Info(
        title = "Concert Tickets API",
        version = "1.0.0",
        description = "API REST pour la gestion de vente de tickets",
        contact = @Contact(
            name = "Équipe de développement",
            email = "support@concert-tickets.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            description = "Serveur de développement",
            url = "http://localhost:8080"
        )
    }
)
public class JerseyConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        
        // Enregistrer le provider Jackson personnalisé
        resources.add(JacksonConfig.class);
        
        // Enregistrer la ressource OpenAPI
        resources.add(OpenApiResource.class);
        
        // ✅ IMPORTANT : Enregistrer manuellement les controllers
        resources.add(rest.controller.EventController.class);
        resources.add(rest.controller.ClientController.class);
        resources.add(rest.controller.OrderController.class);
        resources.add(rest.controller.TicketController.class);
        resources.add(rest.controller.VenueController.class);
        resources.add(rest.controller.CategoryController.class);
        resources.add(rest.controller.OrganizerController.class);
        resources.add(rest.controller.AuthController.class);
        
        // Enregistrer les exception mappers
        resources.add(rest.exception.RestExceptionMapper.class);

        // Filtre CORS (requis pour le frontend Angular sur :4200)
        resources.add(rest.filter.CorsFilter.class);

        resources.add(rest.filter.EntityManagerFilter.class);
        
        return resources;
    }

    @Provider
    public static class JacksonConfig implements ContextResolver<ObjectMapper> {

        private final ObjectMapper objectMapper;

        public JacksonConfig() {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return objectMapper;
        }
    }
}