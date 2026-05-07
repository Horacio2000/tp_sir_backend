package entity;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venues")
@NamedQueries({
    @NamedQuery(
        name = "Venue.findByCity",
        query = "SELECT v FROM Venue v WHERE LOWER(v.city) = LOWER(:city)"
    ),
    @NamedQuery(
        name = "Venue.findByMinCapacity",
        query = "SELECT v FROM Venue v WHERE v.capacity >= :minCapacity ORDER BY v.capacity DESC"
    )
})
public class Venue implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(nullable = true, length = 300)
    private String address;
    
    @Column(nullable = false, length = 100)
    private String city;
    
    @Column(nullable = true, length = 100)
    private String country;
    
    @Column(nullable = true)
    private Integer capacity;
    
    @Column(length = 1000)
    private String description;
    
    // Relation OneToMany avec Event (un lieu peut accueillir plusieurs événements)
    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    private List<Event> events = new ArrayList<>();
    
    // Constructeurs
    public Venue() {
    }
    
    public Venue(String name, String address, String city, String country, Integer capacity) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.country = country;
        this.capacity = capacity;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<Event> getEvents() {
        return events;
    }
    
    public void setEvents(List<Event> events) {
        this.events = events;
    }
    
    @Override
    public String toString() {
        return "Venue{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}