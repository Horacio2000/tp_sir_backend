package rest.controller;

import dao.IClientDao;
import dao.IEventDao;
import dao.IOrderDao;
import dao.ITicketDao;
import dao.impl.ClientDaoImpl;
import dao.impl.EventDaoImpl;
import dao.impl.OrderDaoImpl;
import dao.impl.TicketDaoImpl;
import dto.OrderDto;
import dto.mapper.OrderMapper;
import entity.Event;
import entity.Order;
import entity.Ticket;
import entity.enums.OrderStatus;
import entity.enums.TicketType;
import entity.user.Client;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import rest.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Orders", description = "API de gestion des commandes")
public class OrderController {

    private final IOrderDao  orderDao;
    private final IClientDao clientDao;
    private final IEventDao  eventDao;
    private final ITicketDao ticketDao;

    private static final Map<String, Double> PRICE_MULTIPLIER = Map.of(
        "SIMPLE",  1.0,
        "PREMIUM", 1.5,
        "VIP",     2.5,
        "VVIP",    4.0
    );

    public OrderController() {
        this.orderDao  = new OrderDaoImpl();
        this.clientDao = new ClientDaoImpl();
        this.eventDao  = new EventDaoImpl();
        this.ticketDao = new TicketDaoImpl();
    }

    // ============================================================
    // ENDPOINTS CRUD
    // ============================================================

    @GET
    @Operation(summary = "Liste toutes les commandes")
    public Response getAllOrders() {
        List<Order> orders = orderDao.findAll();
        List<OrderDto> dtos = orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Récupère une commande par ID")
    public Response getOrderById(
            @Parameter(description = "ID de la commande", required = true)
            @PathParam("id") Long id) {
        Order order = orderDao.findOne(id);
        if (order == null) throw new ResourceNotFoundException("Order", "id", id);
        return Response.ok(OrderMapper.toDto(order)).build();
    }

    @POST
    @Operation(summary = "Crée une commande avec ses billets")
    public Response createOrder(OrderDto dto) {

        if (dto.getClientId() == null || dto.getEventId() == null
                || dto.getTicketType() == null || dto.getQuantity() == null
                || dto.getQuantity() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"clientId, eventId, ticketType et quantity sont obligatoires\"}")
                    .build();
        }

        Client client = clientDao.findOne(dto.getClientId());
        if (client == null) throw new ResourceNotFoundException("Client", "id", dto.getClientId());

        Event event = eventDao.findOne(dto.getEventId());
        if (event == null) throw new ResourceNotFoundException("Event", "id", dto.getEventId());

        // Vérification des places disponibles
        if (event.getAvailableTickets() < dto.getQuantity()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Places insuffisantes pour cet événement\"}")
                    .build();
        }

        double multiplier = PRICE_MULTIPLIER.getOrDefault(dto.getTicketType(), 1.0);
        double unitPrice  = Math.round(event.getBasePrice() * multiplier * 100.0) / 100.0;
        double totalPrice = Math.round(unitPrice * dto.getQuantity() * 100.0) / 100.0;

        Order order = new Order(client, totalPrice);
        order.confirm();
        orderDao.save(order);
        System.out.println(">>> Order saved: " + order.getId());

        TicketType type = TicketType.valueOf(dto.getTicketType());
        for (int i = 0; i < dto.getQuantity(); i++) {
            Ticket ticket = new Ticket(type, unitPrice, event);
            ticket.sell();
            order.addTicket(ticket);
            ticketDao.save(ticket);
            System.out.println(">>> Ticket saved: " + ticket.getId());
        }

        System.out.println(">>> Adding loyalty points...");
        clientDao.addLoyaltyPoints(dto.getClientId(), (int) totalPrice);

        System.out.println(">>> Updating available tickets...");
        eventDao.updateAvailableTickets(dto.getEventId(), -dto.getQuantity());

        System.out.println(">>> Building response...");


        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("orderId", order.getId());
        result.put("orderNumber", order.getOrderNumber());
        result.put("totalAmount", order.getTotalAmount());
        return Response.status(Response.Status.CREATED).entity(result).build();
            }


    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprime une commande")
    public Response deleteOrder(@PathParam("id") Long id) {
        Order order = orderDao.findOne(id);
        if (order == null) throw new ResourceNotFoundException("Order", "id", id);
        orderDao.deleteById(id);
        return Response.noContent().build();
    }

    // ============================================================
    // ENDPOINTS MÉTIER
    // ============================================================

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

    @GET
    @Path("/recent")
    @Operation(summary = "Commandes récentes")
    public Response getRecentOrders(@QueryParam("days") @DefaultValue("7") int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Order> orders = orderDao.findRecentOrders(since);
        List<OrderDto> dtos = orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Statistiques des commandes")
    public Response getOrderStats() {
        Double totalRevenue  = orderDao.getTotalRevenue();
        Long confirmedCount  = orderDao.countOrdersByStatus(OrderStatus.CONFIRMED);
        Long pendingCount    = orderDao.countOrdersByStatus(OrderStatus.PENDING);
        Long cancelledCount  = orderDao.countOrdersByStatus(OrderStatus.CANCELLED);

        var stats = new Object() {
            public final Double revenue  = totalRevenue;
            public final Long confirmed  = confirmedCount;
            public final Long pending    = pendingCount;
            public final Long cancelled  = cancelledCount;
            public final Long total      = confirmedCount + pendingCount + cancelledCount;
        };
        return Response.ok(stats).build();
    }

    @GET
    @Path("/top")
    @Operation(summary = "Top commandes par montant")
    public Response getTopOrders(@QueryParam("limit") @DefaultValue("10") int limit) {
        List<Order> orders = orderDao.findTopOrders(limit);
        List<OrderDto> dtos = orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Recherche de commandes")
    public Response searchOrders(
            @QueryParam("clientId") Long clientId,
            @QueryParam("status")   OrderStatus status,
            @QueryParam("minAmount") Double minAmount,
            @QueryParam("maxAmount") Double maxAmount) {
        List<Order> orders = orderDao.findByCriteria(clientId, status, minAmount, maxAmount);
        List<OrderDto> dtos = orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }
}
