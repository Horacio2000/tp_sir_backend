package entity.user;

import entity.Order;
import java.io.Serializable;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@NamedQueries({
    @NamedQuery(
        name = "Client.findByEmail",
        query = "SELECT c FROM Client c WHERE c.email = :email"
    ),
    @NamedQuery(
        name = "Client.findTopByLoyaltyPoints",
        query = "SELECT c FROM Client c ORDER BY c.loyaltyPoints DESC"
    ),
    @NamedQuery(
        name = "Client.findByMinLoyaltyPoints",
        query = "SELECT c FROM Client c WHERE c.loyaltyPoints >= :points"
    )
})
@DiscriminatorValue("CLIENT")
public class Client extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "loyalty_points")
    private Integer loyaltyPoints;
    
    // Relation bidirectionnelle avec Order
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
    
    // Constructeurs
    public Client() {
        super();
        this.loyaltyPoints = 0;
    }
    
    public Client(String email, String password, String firstName, String lastName) {
        super(email, password, firstName, lastName);
        this.loyaltyPoints = 0;
    }
    
    public Client(String email, String password, String firstName, String lastName, String phone) {
        super(email, password, firstName, lastName);
        this.phone = phone;
        this.loyaltyPoints = 0;
    }
    
    // Méthode métier pour ajouter des points de fidélité
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }
    
    // Méthode helper pour ajouter une commande
    public void addOrder(Order order) {
        orders.add(order);
        order.setClient(this);
    }
    
    public void removeOrder(Order order) {
        orders.remove(order);
        order.setClient(null);
    }
    
    // Getters et Setters
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }
    
    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
    
    public List<Order> getOrders() {
        return orders;
    }
    
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "id=" + getId() +
                ", email='" + getEmail() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", phone='" + phone + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                '}';
    }
}