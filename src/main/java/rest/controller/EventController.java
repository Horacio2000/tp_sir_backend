package rest.controller;

import dao.IEventDao;
import dao.impl.EventDaoImpl;
import dto.EventDto;
import dto.mapper.EventMapper;
import entity.Event;
import entity.enums.EventStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import rest.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST pour la gestion des événements
 * Base URL: /api/events
 */
@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Events", description = "API de gestion des événements de concert")
public class EventController {

    private final IEventDao eventDao;

    public EventController() {
        this.eventDao = new EventDaoImpl();
    }

    // ============================================================
    // ENDPOINTS CRUD
    // ============================================================

    /**
     * GET /api/events
     * Récupère tous les événements
     */
    @GET
    @Operation(
        summary = "Liste tous les événements",
        description = "Retourne la liste complète des événements disponibles",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Liste récupérée avec succès",
                content = @Content(schema = @Schema(implementation = EventDto.class))
            )
        }
    )
    public Response getAllEvents() {
        try {
            List<Event> events = eventDao.findAll();
            List<EventDto> dtos = events.stream()
                    .map(EventMapper::toDto)
                    .collect(Collectors.toList());
            
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la récupération des événements: " + e.getMessage())
                    .build();
        }
    }

    /**
     * GET /api/events/{id}
     * Récupère un événement par son ID
     */
    @GET
    @Path("/{id}")
    @Operation(
        summary = "Récupère un événement par ID",
        description = "Retourne les détails d'un événement spécifique",
        responses = {
            @ApiResponse(responseCode = "200", description = "Événement trouvé"),
            @ApiResponse(responseCode = "404", description = "Événement non trouvé")
        }
    )
    public Response getEventById(@PathParam("id") Long id) {
        Event event = eventDao.findOne(id);
        
        if (event == null) {
            throw new ResourceNotFoundException("Event", "id", id);
        }
        
        EventDto dto = EventMapper.toDto(event);
        dto.setTicketsSold(eventDao.countTicketsSold(id));
        dto.setRevenue(eventDao.calculateRevenue(id));
        
        return Response.ok(dto).build();
    }

    /**
     * POST /api/events
     * Crée un nouvel événement
     */
    @POST
    @Operation(
        summary = "Crée un nouvel événement",
        description = "Ajoute un nouvel événement dans la base de données",
        responses = {
            @ApiResponse(responseCode = "201", description = "Événement créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
        }
    )
    public Response createEvent(EventDto dto) {
        // Validation
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Le titre est obligatoire");
        }
        if (dto.getEventDate() == null) {
            throw new BadRequestException("La date de l'événement est obligatoire");
        }
        if (dto.getBasePrice() == null || dto.getBasePrice() <= 0) {
            throw new BadRequestException("Le prix doit être supérieur à 0");
        }
        
        Event event = EventMapper.toEntity(dto);
        eventDao.save(event);
        
        return Response.status(Response.Status.CREATED)
                .entity(EventMapper.toDto(event))
                .build();
    }

    /**
     * PUT /api/events/{id}
     * Met à jour un événement existant
     */
    @PUT
    @Path("/{id}")
    @Operation(
        summary = "Met à jour un événement",
        description = "Modifie un événement existant",
        responses = {
            @ApiResponse(responseCode = "200", description = "Événement mis à jour"),
            @ApiResponse(responseCode = "404", description = "Événement non trouvé")
        }
    )
    public Response updateEvent(
            @PathParam("id") Long id,
            EventDto dto) {
        
        try {
            Event existing = eventDao.findOne(id);
            
            if (existing == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Événement non trouvé")
                        .build();
            }
            
            // Mettre à jour les champs
            existing.setTitle(dto.getTitle());
            existing.setDescription(dto.getDescription());
            existing.setEventDate(dto.getEventDate());
            existing.setStartTime(dto.getStartTime());
            existing.setEndTime(dto.getEndTime());
            existing.setBasePrice(dto.getBasePrice());
            existing.setStatus(dto.getStatus());
            
            Event updated = eventDao.update(existing);
            EventDto updatedDto = EventMapper.toDto(updated);
            
            return Response.ok(updatedDto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    /**
     * DELETE /api/events/{id}
     * Supprime un événement
     */
    @DELETE
    @Path("/{id}")
    @Operation(
        summary = "Supprime un événement",
        description = "Supprime définitivement un événement",
        responses = {
            @ApiResponse(responseCode = "204", description = "Événement supprimé"),
            @ApiResponse(responseCode = "404", description = "Événement non trouvé")
        }
    )
    public Response deleteEvent(@PathParam("id") Long id) {
        Event event = eventDao.findOne(id);
        
        if (event == null) {
            throw new ResourceNotFoundException("Event", "id", id);
        }
        
        eventDao.deleteById(id);
        return Response.noContent().build();
    }

    // ============================================================
    // ENDPOINTS MÉTIER (pas juste CRUD)
    // ============================================================

    /**
     * GET /api/events/available
     * Récupère tous les événements disponibles (avec des tickets)
     */
    @GET
    @Path("/available")
    @Operation(
        summary = "Liste les événements disponibles",
        description = "Retourne uniquement les événements publiés avec des tickets disponibles"
    )
    public Response getAvailableEvents() {
        try {
            List<Event> events = eventDao.findAvailableEvents();
            List<EventDto> dtos = events.stream()
                    .map(EventMapper::toDto)
                    .collect(Collectors.toList());
            
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    /**
     * GET /api/events/city/{city}
     * Recherche des événements par ville
     */
    @GET
    @Path("/city/{city}")
    @Operation(
        summary = "Recherche par ville",
        description = "Retourne tous les événements dans une ville spécifique"
    )
    public Response getEventsByCity(@PathParam("city") String city) {
        try {
            List<Event> events = eventDao.findByCity(city);
            List<EventDto> dtos = events.stream()
                    .map(EventMapper::toDto)
                    .collect(Collectors.toList());
            
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    /**
     * GET /api/events/search
     * Recherche avancée avec plusieurs critères
     */
    @GET
    @Path("/search")
    @Operation(
        summary = "Recherche avancée",
        description = "Recherche d'événements avec plusieurs critères optionnels"
    )
    public Response searchEvents(
            @QueryParam("city") String city,
            @QueryParam("status") EventStatus status,
            @QueryParam("maxPrice") Double maxPrice,
            @QueryParam("fromDate") String fromDateStr) {
        
        try {
            LocalDate fromDate = null;
            if (fromDateStr != null && !fromDateStr.isEmpty()) {
                fromDate = LocalDate.parse(fromDateStr);
            }
            
            List<Event> events = eventDao.findByCriteria(city, status, maxPrice, fromDate);
            List<EventDto> dtos = events.stream()
                    .map(EventMapper::toDto)
                    .collect(Collectors.toList());
            
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    /**
     * GET /api/events/{id}/stats
     * Récupère les statistiques d'un événement
     */
    @GET
    @Path("/{id}/stats")
    @Operation(
        summary = "Statistiques d'un événement",
        description = "Retourne les statistiques de vente pour un événement"
    )
    public Response getEventStats(@PathParam("id") Long id) {
        try {
            Event event = eventDao.findOne(id);
            
            if (event == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
            Long ticketsSold = eventDao.countTicketsSold(id);
            Double revenue = eventDao.calculateRevenue(id);
            
            // Créer un objet de statistiques
            var stats = new Object() {
                public final Long eventId = id;
                public final String title = event.getTitle();
                public final Integer totalTickets = event.getTotalTickets();
                public final Integer availableTickets = event.getAvailableTickets();
                public final Long soldTickets = ticketsSold;
                public final Double totalRevenue = revenue;
                public final Double occupancyRate = 
                    (ticketsSold.doubleValue() / event.getTotalTickets()) * 100;
            };
            
            return Response.ok(stats).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }
}