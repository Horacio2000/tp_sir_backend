package entity;

import java.io.Serializable;
import entity.enums.TicketStatus;
import entity.enums.TicketType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@NamedQueries({
    @NamedQuery(
        name = "Ticket.findByEvent",
        query = "SELECT t FROM Ticket t WHERE t.event.id = :eventId"
    ),
    @NamedQuery(
        name = "Ticket.findByStatus",
        query = "SELECT t FROM Ticket t WHERE t.status = :status"
    ),
    @NamedQuery(
        name = "Ticket.countByEventAndStatus",
        query = "SELECT COUNT(t) FROM Ticket t WHERE t.event.id = :eventId AND t.status = :status"
    )
})
public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type", nullable = false, length = 20)
    private TicketType ticketType;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(name = "qr_code", unique = true, nullable = false, length = 100)
    private String qrCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status;
    
    @Column(name = "issue_date", nullable = false, updatable = false)
    private LocalDateTime issueDate;
    
    // Relation ManyToOne avec Event (bidirectionnelle)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    // Relation ManyToOne avec Order (bidirectionnelle)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
    // Constructeurs
    public Ticket() {
        this.issueDate = LocalDateTime.now();
        this.status = TicketStatus.AVAILABLE;
        this.qrCode = generateQRCode();
    }
    
    public Ticket(TicketType ticketType, Double price, Event event) {
        this.ticketType = ticketType;
        this.price = price;
        this.event = event;
        this.issueDate = LocalDateTime.now();
        this.status = TicketStatus.AVAILABLE;
        this.qrCode = generateQRCode();
    }
    
    // Méthode pour générer un QR code unique
    private String generateQRCode() {
        return "QR-" + UUID.randomUUID().toString();
    }
    
    // Méthodes métier pour changer le statut
    public void reserve() {
        this.status = TicketStatus.RESERVED;
    }
    
    public void sell() {
        this.status = TicketStatus.SOLD;
    }
    
    public void use() {
        this.status = TicketStatus.USED;
    }
    
    public void cancel() {
        this.status = TicketStatus.CANCELLED;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public TicketType getTicketType() {
        return ticketType;
    }
    
    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public String getQrCode() {
        return qrCode;
    }
    
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    
    public TicketStatus getStatus() {
        return status;
    }
    
    public void setStatus(TicketStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getIssueDate() {
        return issueDate;
    }
    
    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", ticketType=" + ticketType +
                ", price=" + price +
                ", status=" + status +
                ", qrCode='" + qrCode + '\'' +
                '}';
    }
}