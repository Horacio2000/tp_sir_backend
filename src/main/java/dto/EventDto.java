package dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import entity.enums.EventStatus;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO pour l'entité Event
 * Utilisé pour les échanges avec le client (API REST)
 */
public class EventDto {

    private Long id;
    private String title;
    private String description;
    private Long organizerId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    
    private Double basePrice;
    private Integer totalTickets;
    private Integer availableTickets;
    private EventStatus status;
    
    // Informations du lieu (dénormalisées pour éviter les jointures)
    private String venueName;
    private String venueCity;
    private Integer venueCapacity;
    private String venueCountry;
    
    // Informations de l'organisateur (dénormalisées)
    private String organizerName;
    private String organizerCompany;
    
    // Statistiques (optionnelles)
    private Long ticketsSold;
    private Double revenue;

    // Constructeur vide (nécessaire pour Jackson)
    public EventDto() {
    }

    // Constructeur avec les champs essentiels
    public EventDto(Long id, String title, LocalDate eventDate, 
                    LocalTime startTime, Double basePrice, EventStatus status) {
        this.id = id;
        this.title = title;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.basePrice = basePrice;
        this.status = status;
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

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }

    public Integer getVenueCapacity() {
        return venueCapacity;
    }

    public void setVenueCapacity(Integer venueCapacity) {
        this.venueCapacity = venueCapacity;
    }

    public String getVenueCountry() {
        return venueCountry;
    }

    public void setVenueCountry(String venueCountry) {
        this.venueCountry = venueCountry;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getOrganizerCompany() {
        return organizerCompany;
    }

    public void setOrganizerCompany(String organizerCompany) {
        this.organizerCompany = organizerCompany;
    }

    public Long getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(Long ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Long getOrganizerId() { 
        return organizerId; 
    }

    public void setOrganizerId(Long organizerId) { 
        this.organizerId = organizerId; 
    }


    @Override
    public String toString() {
        return "EventDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", eventDate=" + eventDate +
                ", status=" + status +
                '}';
    }
}