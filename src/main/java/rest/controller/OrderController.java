package rest.controller;

import dao.IOrderDao;
import dao.impl.OrderDaoImpl;
import dto.OrderDto;
import dto.mapper.OrderMapper;
import entity.Order;
import entity.enums.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import rest.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST pour la gestion des commandes
 * Base URL: /api/orders
 */
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Orders", description = "API de gestion des commandes")
public class OrderController {

    private final IOrderDao orderDao;

    public OrderController() {
        this.orderDao = new OrderDaoImpl();
    }

    // ============================================================
    // ENDPOINTS CRUD
    // ============================================================

    /**
     * GET /api/orders
     * Liste toutes les commandes
     */
    @GET
    @Operation(summary = "Liste toutes les commandes")
    public Response getAllOrders() {
        List<Order> orders = orderDao.findAll();
        List<OrderDto> dtos = orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/orders/{id}
     * Récupère une commande par son ID
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Récupère une commande par ID")
    public Response getOrderById(
            @Parameter(description = "ID de la commande", required = true)
            @PathParam("id") Long id) {
        
        Order order = orderDao.findOne(id);
        
        if (order == null) {
            throw new ResourceNotFoundException("Order", "id", id);
        }
        
        return Response.ok(OrderMapper.toDto(order)).build();
    }

    /**
     * DELETE /api/orders/{id}
     * Supprime une commande
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprime une commande")
    public Response deleteOrder(@PathParam("id") Long id) {
        Order order = orderDao.findOne(id);
        
        if (order == null) {
            throw new ResourceNotFoundException("Order", "id", id);
        }
        
        orderDao.deleteById(id);
        return Response.noContent().build();
    }

    // ============================================================
    // ENDPOINTS MÉTIER
    // ============================================================

    /**
     * GET /api/orders/client/{clientId}
     * Récupère toutes les commandes d'un client
     */
    @GET
    @Path("/client/{clientId}")
    @Operation(summary = "Commandes d'un client")
    public Response getOrdersByClient(@PathParam("clientId") Long clientId) {
        List<Order> orders = orderDao.findByClient(clientId);
        List<OrderDto> dtos = orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/orders/status/{status}
     * Filtre les commandes par statut
     */
    @GET
    @Path("/status/{status}")
    @Operation(summary = "Commandes par statut")
    public Response getOrdersByStatus(@PathParam("status") OrderStatus status) {
        List<Order> orders = orderDao.findByStatus(status);
        List<OrderDto> dtos = orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/orders/recent
     * Commandes récentes (derniers 7 jours)
     */
    @GET
    @Path("/recent")
    @Operation(summary = "Commandes récentes")
    public Response getRecentOrders(
            @QueryParam("days") @DefaultValue("7") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Order> orders = orderDao.findRecentOrders(since);
        List<OrderDto> dtos = orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/orders/stats
     * Statistiques globales des commandes
     */
    @GET
    @Path("/stats")
    @Operation(summary = "Statistiques des commandes")
    public Response getOrderStats() {
        Double totalRevenue = orderDao.getTotalRevenue();
        Long confirmedCount = orderDao.countOrdersByStatus(OrderStatus.CONFIRMED);
        Long pendingCount = orderDao.countOrdersByStatus(OrderStatus.PENDING);
        Long cancelledCount = orderDao.countOrdersByStatus(OrderStatus.CANCELLED);
        
        var stats = new Object() {
            public final Double revenue = totalRevenue;
            public final Long confirmed = confirmedCount;
            public final Long pending = pendingCount;
            public final Long cancelled = cancelledCount;
            public final Long total = confirmedCount + pendingCount + cancelledCount;
        };
        
        return Response.ok(stats).build();
    }

    /**
     * GET /api/orders/top
     * Top commandes par montant
     */
    @GET
    @Path("/top")
    @Operation(summary = "Top commandes par montant")
    public Response getTopOrders(
            @QueryParam("limit") @DefaultValue("10") int limit) {
        
        List<Order> orders = orderDao.findTopOrders(limit);
        List<OrderDto> dtos = orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/orders/search
     * Recherche de commandes avec critères
     */
    @GET
    @Path("/search")
    @Operation(summary = "Recherche de commandes")
    public Response searchOrders(
            @QueryParam("clientId") Long clientId,
            @QueryParam("status") OrderStatus status,
            @QueryParam("minAmount") Double minAmount,
            @QueryParam("maxAmount") Double maxAmount) {
        
        List<Order> orders = orderDao.findByCriteria(clientId, status, minAmount, maxAmount);
        List<OrderDto> dtos = orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }
}