package ru.tomtrix.fvds.db;
// Generated 31-Dec-2022 18:01:11 by Hibernate Tools 3.2.2.GA


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Usr generated by hbm2java
 */
@Entity
@Table(name="usr"
    ,schema="public"
    , uniqueConstraints = @UniqueConstraint(columnNames="username") 
)
public class Usr  implements java.io.Serializable {


     private Long userId;
     private String username;
     private Set<Category> categories = new HashSet<Category>(0);
     private Set<Person> persons = new HashSet<Person>(0);

    public Usr() {
    }

	
    public Usr(String username) {
        this.username = username;
    }
    public Usr(String username, Set<Category> categories, Set<Person> persons) {
       this.username = username;
       this.categories = categories;
       this.persons = persons;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="user_id", unique=true, nullable=false)
    public Long getUserId() {
        return this.userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    @Column(name="username", unique=true, nullable=false, length=64)
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="usr")
    public Set<Category> getCategories() {
        return this.categories;
    }
    
    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="usr")
    public Set<Person> getPersons() {
        return this.persons;
    }
    
    public void setPersons(Set<Person> persons) {
        this.persons = persons;
    }




}


