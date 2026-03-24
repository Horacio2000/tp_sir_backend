package rest.controller;

import dao.ITicketDao;
import dao.impl.TicketDaoImpl;
import dto.TicketDto;
import dto.mapper.TicketMapper;
import entity.Ticket;
import entity.enums.TicketStatus;
import entity.enums.TicketType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import rest.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST pour la gestion des tickets
 * Base URL: /api/tickets
 */
@Path("/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tickets", description = "API de gestion des tickets")
public class TicketController {

    private final ITicketDao ticketDao;

    public TicketController() {
        this.ticketDao = new TicketDaoImpl();
    }

    // ============================================================
    // ENDPOINTS CRUD
    // ============================================================

    /**
     * GET /api/tickets
     * Liste tous les tickets
     */
    @GET
    @Operation(summary = "Liste tous les tickets")
    public Response getAllTickets(
            @QueryParam("limit") @DefaultValue("100") int limit) {
        
        List<Ticket> tickets = ticketDao.findAll();
        List<TicketDto> dtos = tickets.stream()
                .limit(limit)
                .map(TicketMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/tickets/{id}
     * Récupère un ticket par son ID
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Récupère un ticket par ID")
    public Response getTicketById(
            @Parameter(description = "ID du ticket", required = true)
            @PathParam("id") Long id) {
        
        Ticket ticket = ticketDao.findOne(id);
        
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket", "id", id);
        }
        
        return Response.ok(TicketMapper.toDto(ticket)).build();
    }

    /**
     * DELETE /api/tickets/{id}
     * Supprime un ticket
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprime un ticket")
    public Response deleteTicket(@PathParam("id") Long id) {
        Ticket ticket = ticketDao.findOne(id);
        
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket", "id", id);
        }
        
        ticketDao.deleteById(id);
        return Response.noContent().build();
    }

    // ============================================================
    // ENDPOINTS MÉTIER
    // ============================================================

    /**
     * GET /api/tickets/event/{eventId}
     * Récupère tous les tickets d'un événement
     */
    @GET
    @Path("/event/{eventId}")
    @Operation(summary = "Tickets d'un événement")
    public Response getTicketsByEvent(@PathParam("eventId") Long eventId) {
        List<Ticket> tickets = ticketDao.findByEvent(eventId);
        List<TicketDto> dtos = tickets.stream()
                .map(TicketMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/tickets/event/{eventId}/available
     * Tickets disponibles pour un événement
     */
    @GET
    @Path("/event/{eventId}/available")
    @Operation(summary = "Tickets disponibles pour un événement")
    public Response getAvailableTicketsByEvent(@PathParam("eventId") Long eventId) {
        List<Ticket> tickets = ticketDao.findAvailableTicketsByEvent(eventId);
        List<TicketDto> dtos = tickets.stream()
                .map(TicketMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/tickets/status/{status}
     * Filtre les tickets par statut
     */
    @GET
    @Path("/status/{status}")
    @Operation(summary = "Tickets par statut")
    public Response getTicketsByStatus(@PathParam("status") TicketStatus status) {
        List<Ticket> tickets = ticketDao.findByStatus(status);
        List<TicketDto> dtos = tickets.stream()
                .map(TicketMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/tickets/qr/{qrCode}
     * Recherche un ticket par QR code
     */
    @GET
    @Path("/qr/{qrCode}")
    @Operation(summary = "Recherche par QR code")
    public Response getTicketByQrCode(@PathParam("qrCode") String qrCode) {
        Ticket ticket = ticketDao.findByQrCode(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "qrCode", qrCode));
        
        return Response.ok(TicketMapper.toDto(ticket)).build();
    }

    /**
     * GET /api/tickets/search
     * Recherche avancée de tickets
     */
    @GET
    @Path("/search")
    @Operation(summary = "Recherche de tickets")
    public Response searchTickets(
            @QueryParam("eventId") Long eventId,
            @QueryParam("type") TicketType type,
            @QueryParam("status") TicketStatus status,
            @QueryParam("minPrice") Double minPrice,
            @QueryParam("maxPrice") Double maxPrice) {
        
        List<Ticket> tickets = ticketDao.findByCriteria(eventId, type, status, minPrice, maxPrice);
        List<TicketDto> dtos = tickets.stream()
                .map(TicketMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * POST /api/tickets/{id}/validate
     * Valide/utilise un ticket (scan QR code)
     */
    @POST
    @Path("/{id}/validate")
    @Operation(summary = "Valide un ticket")
    public Response validateTicket(@PathParam("id") Long id) {
        Ticket ticket = ticketDao.findOne(id);
        
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket", "id", id);
        }
        
        if (ticket.getStatus() != TicketStatus.SOLD) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ce ticket ne peut pas être validé (statut: " + ticket.getStatus() + ")")
                    .build();
        }
        
        ticket.use();
        ticketDao.update(ticket);
        
        return Response.ok(TicketMapper.toDto(ticket)).build();
    }
}