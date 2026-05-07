package rest.controller;

import dao.IClientDao;
import dao.impl.ClientDaoImpl;
import dto.ClientDto;
import dto.LoginDto;
import dto.mapper.ClientMapper;
import entity.user.Client;
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
 * Controller REST pour la gestion des clients
 * Base URL: /api/clients
 */
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Clients", description = "API de gestion des clients")
public class ClientController {

    private final IClientDao clientDao;

    public ClientController() {
        this.clientDao = new ClientDaoImpl();
    }

    // ============================================================
    // ENDPOINTS CRUD
    // ============================================================

    /**
     * GET /api/clients
     * Liste tous les clients
     */
    @GET
    @Operation(summary = "Liste tous les clients")
    public Response getAllClients() {
        List<Client> clients = clientDao.findAll();
        List<ClientDto> dtos = clients.stream()
                .map(ClientMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/clients/{id}
     * Récupère un client par son ID
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Récupère un client par ID")
    public Response getClientById(
            @Parameter(description = "ID du client", required = true)
            @PathParam("id") Long id) {
        
        Client client = clientDao.findOne(id);
        
        if (client == null) {
            throw new ResourceNotFoundException("Client", "id", id);
        }
        
        ClientDto dto = ClientMapper.toDto(client);
        
        dto.setTotalOrders(clientDao.getTotalOrdersCount(id));
        dto.setTotalSpent(clientDao.getTotalSpent(id));
        
        return Response.ok(dto).build();
    }

    /**
     * POST /api/clients
     * Crée un nouveau client
     */
    @POST
    @Operation(summary = "Crée un nouveau client")
    public Response createClient(ClientDto dto) {
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new BadRequestException("L'email est obligatoire");
        }
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            throw new BadRequestException("Le prénom est obligatoire");
        }
        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            throw new BadRequestException("Le nom est obligatoire");
        }
        
        clientDao.findByEmail(dto.getEmail()).ifPresent(existing -> {
            throw new BadRequestException("Un client avec cet email existe déjà");
        });
        
        Client client = ClientMapper.toEntity(dto);
        client.setPassword(dto.getPassword() != null ? dto.getPassword() : "");
        clientDao.save(client);
        
        return Response.status(Response.Status.CREATED)
                .entity(ClientMapper.toDto(client))
                .build();
    }

    @POST
    @Path("/login")
    @Operation(summary = "Authentifie un client par email et mot de passe")
    public Response login(LoginDto dto) {
        if (dto.getEmail() == null || dto.getPassword() == null) {
            throw new BadRequestException("Email et mot de passe obligatoires");
        }

        Client client = clientDao.findByEmail(dto.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Client", "email", dto.getEmail()));

        if (!dto.getPassword().equals(client.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\":\"Email ou mot de passe incorrect\"}")
                .build();
        }

        ClientDto clientDto = ClientMapper.toDto(client);
        clientDto.setTotalOrders(clientDao.getTotalOrdersCount(client.getId()));
        clientDto.setTotalSpent(clientDao.getTotalSpent(client.getId()));

        return Response.ok(clientDto).build();
    }


    /**
     * PUT /api/clients/{id}
     * Met à jour un client
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Met à jour un client")
    public Response updateClient(@PathParam("id") Long id, ClientDto dto) {
        Client existing = clientDao.findOne(id);
        
        if (existing == null) {
            throw new ResourceNotFoundException("Client", "id", id);
        }
        
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setPhone(dto.getPhone());
        
        Client updated = clientDao.update(existing);
        
        return Response.ok(ClientMapper.toDto(updated)).build();
    }

    /**
     * DELETE /api/clients/{id}
     * Supprime un client
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprime un client")
    public Response deleteClient(@PathParam("id") Long id) {
        Client client = clientDao.findOne(id);
        
        if (client == null) {
            throw new ResourceNotFoundException("Client", "id", id);
        }
        
        clientDao.deleteById(id);
        return Response.noContent().build();
    }

    // ============================================================
    // ENDPOINTS MÉTIER
    // ============================================================

    /**
     * GET /api/clients/email/{email}
     * Recherche un client par email
     */
    @GET
    @Path("/email/{email}")
    @Operation(summary = "Recherche un client par email")
    public Response getClientByEmail(@PathParam("email") String email) {
        Client client = clientDao.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", email));
        
        ClientDto dto = ClientMapper.toDto(client);
        dto.setTotalOrders(clientDao.getTotalOrdersCount(client.getId()));
        dto.setTotalSpent(clientDao.getTotalSpent(client.getId()));
        
        return Response.ok(dto).build();
    }

    /**
     * GET /api/clients/top
     * Top clients par points de fidélité
     */
    @GET
    @Path("/top")
    @Operation(summary = "Top clients par points de fidélité")
    public Response getTopClients(
            @QueryParam("limit") @DefaultValue("10") int limit) {
        
        List<Client> clients = clientDao.findTopClients();
        List<ClientDto> dtos = clients.stream()
                .limit(limit)
                .map(ClientMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * POST /api/clients/{id}/loyalty
     * Ajoute des points de fidélité
     */
    @POST
    @Path("/{id}/loyalty")
    @Operation(summary = "Ajoute des points de fidélité")
    public Response addLoyaltyPoints(
            @PathParam("id") Long id,
            @QueryParam("points") int points) {
        
        if (points <= 0) {
            throw new BadRequestException("Le nombre de points doit être positif");
        }
        
        Client client = clientDao.findOne(id);
        if (client == null) {
            throw new ResourceNotFoundException("Client", "id", id);
        }
        
        clientDao.addLoyaltyPoints(id, points);
        
        client = clientDao.findOne(id);
        
        return Response.ok(ClientMapper.toDto(client)).build();
    }

    /**
     * GET /api/clients/{id}/stats
     * Statistiques d'un client
     */
    @GET
    @Path("/{id}/stats")
    @Operation(summary = "Statistiques d'un client")
    public Response getClientStats(@PathParam("id") Long id) {
        Client client = clientDao.findOne(id);
        
        if (client == null) {
            throw new ResourceNotFoundException("Client", "id", id);
        }
        
        int totalOrders = clientDao.getTotalOrdersCount(id);
        Double totalSpent = clientDao.getTotalSpent(id);
        
        var stats = new Object() {
            public final Long clientId = id;
            public final String fullName = client.getFullName();
            public final String email = client.getEmail();
            public final Integer loyaltyPoints = client.getLoyaltyPoints();
            public final Integer orders = totalOrders;
            public final Double spent = totalSpent;
            public final Double averageOrderValue = 
                totalOrders > 0 ? totalSpent / totalOrders : 0.0;
        };
        
        return Response.ok(stats).build();
    }

    /**
     * GET /api/clients/search
     * Recherche de clients avec critères
     */
    @GET
    @Path("/search")
    @Operation(summary = "Recherche de clients")
    public Response searchClients(
            @QueryParam("firstName") String firstName,
            @QueryParam("lastName") String lastName,
            @QueryParam("email") String email) {
        
        List<Client> clients = clientDao.findByCriteria(firstName, lastName, email);
        List<ClientDto> dtos = clients.stream()
                .map(ClientMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }
}