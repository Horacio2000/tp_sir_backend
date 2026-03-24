package rest.controller;

import dao.IOrganizerDao;
import dao.impl.OrganizerDaoImpl;
import dto.OrganizerDto;
import dto.mapper.OrganizerMapper;
import entity.user.Organizer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import rest.exception.BadRequestException;
import rest.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST pour la gestion des organisateurs
 * Base URL: /api/organizers
 */
@Path("/organizers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Organizers", description = "API de gestion des organisateurs d'événements")
public class OrganizerController {

    private final IOrganizerDao organizerDao;

    public OrganizerController() {
        this.organizerDao = new OrganizerDaoImpl();
    }

    // ============================================================
    // ENDPOINTS CRUD
    // ============================================================

    /**
     * GET /api/organizers
     * Liste tous les organisateurs
     */
    @GET
    @Operation(summary = "Liste tous les organisateurs")
    public Response getAllOrganizers() {
        List<Organizer> organizers = organizerDao.findAll();
        List<OrganizerDto> dtos = organizers.stream()
                .map(organizer -> {
                    OrganizerDto dto = OrganizerMapper.toDto(organizer);
                    dto.setEventCount(organizerDao.getEventCount(organizer.getId()));
                    dto.setTotalRevenue(organizerDao.getTotalRevenue(organizer.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/organizers/{id}
     * Récupère un organisateur par son ID
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Récupère un organisateur par ID")
    public Response getOrganizerById(
            @Parameter(description = "ID de l'organisateur", required = true)
            @PathParam("id") Long id) {
        
        Organizer organizer = organizerDao.findOne(id);
        
        if (organizer == null) {
            throw new ResourceNotFoundException("Organizer", "id", id);
        }
        
        OrganizerDto dto = OrganizerMapper.toDto(organizer);
        dto.setEventCount(organizerDao.getEventCount(id));
        dto.setTotalRevenue(organizerDao.getTotalRevenue(id));
        
        return Response.ok(dto).build();
    }

    /**
     * POST /api/organizers
     * Crée un nouvel organisateur
     */
    @POST
    @Operation(summary = "Crée un nouvel organisateur")
    public Response createOrganizer(OrganizerDto dto) {
        // Validation
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new BadRequestException("L'email est obligatoire");
        }
        if (dto.getCompanyName() == null || dto.getCompanyName().trim().isEmpty()) {
            throw new BadRequestException("Le nom de l'entreprise est obligatoire");
        }
        if (dto.getSiret() != null && dto.getSiret().length() != 14) {
            throw new BadRequestException("Le SIRET doit contenir 14 caractères");
        }
        
        // Vérifier que l'email n'existe pas déjà
        organizerDao.findByEmail(dto.getEmail()).ifPresent(existing -> {
            throw new BadRequestException("Un organisateur avec cet email existe déjà");
        });
        
        // Vérifier que le SIRET n'existe pas déjà
        if (dto.getSiret() != null) {
            organizerDao.findBySiret(dto.getSiret()).ifPresent(existing -> {
                throw new BadRequestException("Un organisateur avec ce SIRET existe déjà");
            });
        }
        
        Organizer organizer = OrganizerMapper.toEntity(dto);
        organizerDao.save(organizer);
        
        return Response.status(Response.Status.CREATED)
                .entity(OrganizerMapper.toDto(organizer))
                .build();
    }

    /**
     * PUT /api/organizers/{id}
     * Met à jour un organisateur
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Met à jour un organisateur")
    public Response updateOrganizer(
            @PathParam("id") Long id,
            OrganizerDto dto) {
        
        Organizer existing = organizerDao.findOne(id);
        
        if (existing == null) {
            throw new ResourceNotFoundException("Organizer", "id", id);
        }
        
        // Mise à jour des champs
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setCompanyName(dto.getCompanyName());
        existing.setBankAccount(dto.getBankAccount());
        
        Organizer updated = organizerDao.update(existing);
        
        return Response.ok(OrganizerMapper.toDto(updated)).build();
    }

    /**
     * DELETE /api/organizers/{id}
     * Supprime un organisateur
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprime un organisateur")
    public Response deleteOrganizer(@PathParam("id") Long id) {
        Organizer organizer = organizerDao.findOne(id);
        
        if (organizer == null) {
            throw new ResourceNotFoundException("Organizer", "id", id);
        }
        
        organizerDao.deleteById(id);
        return Response.noContent().build();
    }

    // ============================================================
    // ENDPOINTS MÉTIER
    // ============================================================

    /**
     * GET /api/organizers/email/{email}
     * Recherche un organisateur par email
     */
    @GET
    @Path("/email/{email}")
    @Operation(summary = "Recherche par email")
    public Response getOrganizerByEmail(@PathParam("email") String email) {
        Organizer organizer = organizerDao.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", "email", email));
        
        OrganizerDto dto = OrganizerMapper.toDto(organizer);
        dto.setEventCount(organizerDao.getEventCount(organizer.getId()));
        dto.setTotalRevenue(organizerDao.getTotalRevenue(organizer.getId()));
        
        return Response.ok(dto).build();
    }

    /**
     * GET /api/organizers/siret/{siret}
     * Recherche un organisateur par SIRET
     */
    @GET
    @Path("/siret/{siret}")
    @Operation(summary = "Recherche par SIRET")
    public Response getOrganizerBySiret(@PathParam("siret") String siret) {
        Organizer organizer = organizerDao.findBySiret(siret)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", "siret", siret));
        
        OrganizerDto dto = OrganizerMapper.toDto(organizer);
        dto.setEventCount(organizerDao.getEventCount(organizer.getId()));
        dto.setTotalRevenue(organizerDao.getTotalRevenue(organizer.getId()));
        
        return Response.ok(dto).build();
    }

    /**
     * GET /api/organizers/company/{companyName}
     * Recherche par nom d'entreprise
     */
    @GET
    @Path("/company/{companyName}")
    @Operation(summary = "Recherche par nom d'entreprise")
    public Response getOrganizersByCompanyName(@PathParam("companyName") String companyName) {
        List<Organizer> organizers = organizerDao.findByCompanyName(companyName);
        List<OrganizerDto> dtos = organizers.stream()
                .map(organizer -> {
                    OrganizerDto dto = OrganizerMapper.toDto(organizer);
                    dto.setEventCount(organizerDao.getEventCount(organizer.getId()));
                    dto.setTotalRevenue(organizerDao.getTotalRevenue(organizer.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/organizers/top
     * Top organisateurs par nombre d'événements
     */
    @GET
    @Path("/top")
    @Operation(summary = "Top organisateurs")
    public Response getTopOrganizers(
            @QueryParam("limit") @DefaultValue("10") int limit) {
        
        List<Organizer> organizers = organizerDao.findTopOrganizers(limit);
        List<OrganizerDto> dtos = organizers.stream()
                .map(organizer -> {
                    OrganizerDto dto = OrganizerMapper.toDto(organizer);
                    dto.setEventCount(organizerDao.getEventCount(organizer.getId()));
                    dto.setTotalRevenue(organizerDao.getTotalRevenue(organizer.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/organizers/{id}/stats
     * Statistiques d'un organisateur
     */
    @GET
    @Path("/{id}/stats")
    @Operation(summary = "Statistiques d'un organisateur")
    public Response getOrganizerStats(@PathParam("id") Long id) {
        Organizer organizer = organizerDao.findOne(id);
        
        if (organizer == null) {
            throw new ResourceNotFoundException("Organizer", "id", id);
        }
        
        int eventCount = organizerDao.getEventCount(id);
        Double totalRevenue = organizerDao.getTotalRevenue(id);
        
        var stats = new Object() {
            public final Long organizerId = id;
            public final String fullName = organizer.getFullName();
            public final String companyName = organizer.getCompanyName();
            public final String email = organizer.getEmail();
            public final Integer events = eventCount;
            public final Double revenue = totalRevenue;
            public final Double averageRevenuePerEvent = 
                eventCount > 0 ? totalRevenue / eventCount : 0.0;
        };
        
        return Response.ok(stats).build();
    }

    /**
     * GET /api/organizers/search
     * Recherche d'organisateurs avec critères
     */
    @GET
    @Path("/search")
    @Operation(summary = "Recherche d'organisateurs")
    public Response searchOrganizers(
            @QueryParam("companyName") String companyName,
            @QueryParam("city") String city) {
        
        List<Organizer> organizers = organizerDao.findByCriteria(companyName, city);
        List<OrganizerDto> dtos = organizers.stream()
                .map(organizer -> {
                    OrganizerDto dto = OrganizerMapper.toDto(organizer);
                    dto.setEventCount(organizerDao.getEventCount(organizer.getId()));
                    dto.setTotalRevenue(organizerDao.getTotalRevenue(organizer.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }
}