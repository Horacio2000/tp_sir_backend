package rest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import java.util.HashSet;
import java.util.Set;

/**
 * Configuration de l'application REST Jersey
 * @ApplicationPath définit le chemin de base pour tous les endpoints REST
 */
@ApplicationPath("/api")
public class JerseyConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        
        // Enregistrer le provider Jackson personnalisé
        resources.add(JacksonConfig.class);
        
        return resources;
    }

    /**
     * Configuration de Jackson pour la sérialisation JSON
     */
    @Provider
    public static class JacksonConfig implements ContextResolver<ObjectMapper> {

        private final ObjectMapper objectMapper;

        public JacksonConfig() {
            objectMapper = new ObjectMapper();
            
            // Support des types Java 8 Date/Time (LocalDate, LocalDateTime, etc.)
            objectMapper.registerModule(new JavaTimeModule());
            
            // Écrire les dates en format ISO-8601 au lieu de timestamps
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            
            // Indenter le JSON pour le rendre lisible
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return objectMapper;
        }
    }
}