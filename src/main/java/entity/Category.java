package entity;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@NamedQueries({
    @NamedQuery(
        name = "Category.findByName",
        query = "SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(:name)"
    ),
    @NamedQuery(
        name = "Category.findPopular",
        query = "SELECT c FROM Category c WHERE SIZE(c.events) > 0 ORDER BY SIZE(c.events) DESC"
    )
})
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    // Relation ManyToMany avec Event (on la définira plus tard)
    @ManyToMany(mappedBy = "categories")
    private List<Event> events = new ArrayList<>();
    
    // Constructeurs
    public Category() {
    }
    
    public Category(String name) {
        this.name = name;
    }
    
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
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
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}