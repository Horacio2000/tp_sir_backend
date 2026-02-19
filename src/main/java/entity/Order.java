package entity;

import java.io.Serializable;
import entity.enums.OrderStatus;
import entity.user.Client;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "Order.findByClient",
        query = "SELECT o FROM Order o WHERE o.client.id = :clientId ORDER BY o.orderDate DESC"
    ),
    @NamedQuery(
        name = "Order.findByStatus",
        query = "SELECT o FROM Order o WHERE o.status = :status"
    ),
    @NamedQuery(
        name = "Order.findRecentOrders",
        query = "SELECT o FROM Order o WHERE o.orderDate >= :date ORDER BY o.orderDate DESC"
    )
})
@Table(name = "orders")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;
    
    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;
    
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;
    
    // Relation ManyToOne avec Client (bidirectionnelle)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    // Relation OneToOne avec Payment
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "payment_id", unique = true)
    private Payment payment;
    
    // Relation OneToMany avec Ticket (bidirectionnelle)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();
    
    // Constructeurs
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.orderNumber = generateOrderNumber();
    }
    
    public Order(Client client, Double totalAmount) {
        this.client = client;
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.orderNumber = generateOrderNumber();
    }
    
    // Méthode pour générer un numéro de commande unique
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
    
    // Méthodes helper pour gérer les tickets
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setOrder(this);
    }
    
    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
        ticket.setOrder(null);
    }
    
    // Méthode métier pour calculer le montant total
    public void calculateTotalAmount() {
        this.totalAmount = tickets.stream()
                .mapToDouble(Ticket::getPrice)
                .sum();
    }
    
    // Méthodes métier pour changer le statut
    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
    }
    
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
    
    public void refund() {
        this.status = OrderStatus.REFUNDED;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public Payment getPayment() {
        return payment;
    }
    
    public void setPayment(Payment payment) {
        this.payment = payment;
        if (payment != null) {
            payment.setOrder(this);
        }
    }
    
    public List<Ticket> getTickets() {
        return tickets;
    }
    
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
}