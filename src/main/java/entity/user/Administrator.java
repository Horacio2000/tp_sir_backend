package entity.user;

import jakarta.persistence.*;

@Entity
@Table(name = "administrators")
@DiscriminatorValue("ADMIN")
public class Administrator extends User {
    
    @Column(name = "access_level", length = 50)
    private String accessLevel;
    
    @Column(length = 100)
    private String department;
    
    // Constructeurs
    public Administrator() {
        super();
    }
    
    public Administrator(String email, String password, String firstName, String lastName) {
        super(email, password, firstName, lastName);
        this.accessLevel = "FULL";
    }
    
    public Administrator(String email, String password, String firstName, String lastName, 
                         String accessLevel, String department) {
        super(email, password, firstName, lastName);
        this.accessLevel = accessLevel;
        this.department = department;
    }
    
    // Getters et Setters
    public String getAccessLevel() {
        return accessLevel;
    }
    
    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    @Override
    public String toString() {
        return "Administrator{" +
                "id=" + getId() +
                ", email='" + getEmail() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", accessLevel='" + accessLevel + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}