package entity.user;

import entity.Event;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizers")
@DiscriminatorValue("ORGANIZER")
public class Organizer extends User {
    
    @Column(name = "company_name", length = 200)
    private String companyName;
    
    @Column(length = 14, unique = true)
    private String siret;
    
    @Column(name = "bank_account", length = 34)
    private String bankAccount;
    
    // Relation bidirectionnelle avec Event
    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();
    
    // Constructeurs
    public Organizer() {
        super();
    }
    
    public Organizer(String email, String password, String firstName, String lastName) {
        super(email, password, firstName, lastName);
    }
    
    public Organizer(String email, String password, String firstName, String lastName, 
                     String companyName, String siret) {
        super(email, password, firstName, lastName);
        this.companyName = companyName;
        this.siret = siret;
    }
    
    // Méthode helper pour ajouter un événement
    public void addEvent(Event event) {
        events.add(event);
        event.setOrganizer(this);
    }
    
    public void removeEvent(Event event) {
        events.remove(event);
        event.setOrganizer(null);
    }
    
    // Getters et Setters
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
    
    public List<Event> getEvents() {
        return events;
    }
    
    public void setEvents(List<Event> events) {
        this.events = events;
    }
    
    @Override
    public String toString() {
        return "Organizer{" +
                "id=" + getId() +
                ", email='" + getEmail() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", companyName='" + companyName + '\'' +
                ", siret='" + siret + '\'' +
                '}';
    }
}