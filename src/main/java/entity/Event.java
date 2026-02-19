package entity;

import entity.enums.EventStatus;
import entity.user.Organizer;
import java.io.Serializable;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "events")
@NamedQueries({
    @NamedQuery(
        name = "Event.findByOrganizer",
        query = "SELECT e FROM Event e WHERE e.organizer.id = :organizerId"
    ),
    @NamedQuery(
        name = "Event.findUpcoming",
        query = "SELECT e FROM Event e WHERE e.eventDate >= CURRENT_DATE " +
                "AND e.status = 'PUBLISHED' ORDER BY e.eventDate ASC"
    ),
    @NamedQuery(
        name = "Event.findByVenueCity",
        query = "SELECT e FROM Event e WHERE e.venue.city = :city"
    ),
    @NamedQuery(
        name = "Event.countAll",
        query = "SELECT COUNT(e) FROM Event e"
    )
})
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time")
    private LocalTime endTime;
    
    @Column(name = "base_price", nullable = false)
    private Double basePrice;
    
    @Column(name = "total_tickets", nullable = false)
    private Integer totalTickets;
    
    @Column(name = "available_tickets", nullable = false)
    private Integer availableTickets;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatus status;
    
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Relation ManyToOne avec Venue
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;
    
    // Relation ManyToOne avec Organizer (bidirectionnelle)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;
    
    // Relation ManyToMany avec Category
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "event_category",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();
    
    // Relation OneToMany avec Ticket
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();
    
    // Constructeurs
    public Event() {
        this.createdAt = LocalDateTime.now();
        this.status = EventStatus.DRAFT;
    }
    
    public Event(String title, LocalDate eventDate, LocalTime startTime, Double basePrice, 
                 Integer totalTickets, Venue venue, Organizer organizer) {
        this.title = title;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.basePrice = basePrice;
        this.totalTickets = totalTickets;
        this.availableTickets = totalTickets;
        this.venue = venue;
        this.organizer = organizer;
        this.status = EventStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
    }
    
    // Méthodes helper pour gérer les relations
    public void addCategory(Category category) {
        categories.add(category);
        category.getEvents().add(this);
    }
    
    public void removeCategory(Category category) {
        categories.remove(category);
        category.getEvents().remove(this);
    }
    
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setEvent(this);
    }
    
    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
        ticket.setEvent(null);
    }
    
    // Méthode métier pour décrémenter les tickets disponibles
    public boolean decreaseAvailableTickets(int count) {
        if (availableTickets >= count) {
            availableTickets -= count;
            return true;
        }
        return false;
    }
    
    // Méthode métier pour incrémenter les tickets disponibles (annulation)
    public void increaseAvailableTickets(int count) {
        availableTickets += count;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getEventDate() {
        return eventDate;
    }
    
    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public Double getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }
    
    public Integer getTotalTickets() {
        return totalTickets;
    }
    
    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }
    
    public Integer getAvailableTickets() {
        return availableTickets;
    }
    
    public void setAvailableTickets(Integer availableTickets) {
        this.availableTickets = availableTickets;
    }
    
    public EventStatus getStatus() {
        return status;
    }
    
    public void setStatus(EventStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Venue getVenue() {
        return venue;
    }
    
    public void setVenue(Venue venue) {
        this.venue = venue;
    }
    
    public Organizer getOrganizer() {
        return organizer;
    }
    
    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }
    
    public List<Category> getCategories() {
        return categories;
    }
    
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    
    public List<Ticket> getTickets() {
        return tickets;
    }
    
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
    
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", eventDate=" + eventDate +
                ", basePrice=" + basePrice +
                ", availableTickets=" + availableTickets +
                ", status=" + status +
                '}';
    }
}