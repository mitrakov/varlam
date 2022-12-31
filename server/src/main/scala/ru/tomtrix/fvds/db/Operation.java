package ru.tomtrix.fvds.db;
// Generated 31-Dec-2022 18:01:11 by Hibernate Tools 3.2.2.GA


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Operation generated by hbm2java
 */
@Entity
@Table(name="operation"
    ,schema="public"
)
public class Operation  implements java.io.Serializable {


     private Long operationId;
     private Item item;
     private Person person;
     private Date time;
     private int summa;
     private String currency;

    public Operation() {
    }

	
    public Operation(Item item, Date time, int summa, String currency) {
        this.item = item;
        this.time = time;
        this.summa = summa;
        this.currency = currency;
    }
    public Operation(Item item, Person person, Date time, int summa, String currency) {
       this.item = item;
       this.person = person;
       this.time = time;
       this.summa = summa;
       this.currency = currency;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="operation_id", unique=true, nullable=false)
    public Long getOperationId() {
        return this.operationId;
    }
    
    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="item_id", nullable=false)
    public Item getItem() {
        return this.item;
    }
    
    public void setItem(Item item) {
        this.item = item;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="person_id")
    public Person getPerson() {
        return this.person;
    }
    
    public void setPerson(Person person) {
        this.person = person;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="time", nullable=false, length=29)
    public Date getTime() {
        return this.time;
    }
    
    public void setTime(Date time) {
        this.time = time;
    }
    
    @Column(name="summa", nullable=false)
    public int getSumma() {
        return this.summa;
    }
    
    public void setSumma(int summa) {
        this.summa = summa;
    }
    
    @Column(name="currency", nullable=false, length=3)
    public String getCurrency() {
        return this.currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }




}


