package dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * DTO pour l'entité Organizer
 */
public class OrganizerDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String companyName;
    private String siret;
    private String bankAccount;
    private String password;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    // Statistiques
    private Integer eventCount;
    private Double totalRevenue;

    // Constructeurs
    public OrganizerDto() {
    }

    public OrganizerDto(Long id, String email, String companyName) {
        this.id = id;
        this.email = email;
        this.companyName = companyName;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    // Getter/Setter
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getEventCount() {
        return eventCount;
    }

    public void setEventCount(Integer eventCount) {
        this.eventCount = eventCount;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    // Méthode utilitaire
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "OrganizerDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", companyName='" + companyName + '\'' +
                ", eventCount=" + eventCount +
                '}';
    }
}