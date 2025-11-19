package com.zizto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "Services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "monthly_cost")
    private BigDecimal monthlyCost;
    
    @ManyToMany(mappedBy = "services")
    private Set<Subscriber> subscribers;

    public Service() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getMonthlyCost() { return monthlyCost; }
    public void setMonthlyCost(BigDecimal monthlyCost) { this.monthlyCost = monthlyCost; }
    public Set<Subscriber> getSubscribers() { return subscribers; }
    public void setSubscribers(Set<Subscriber> subscribers) { this.subscribers = subscribers; }

    @Override
    public String toString() {
        return "Услуга [ID=" + id + ", Название=" + name + ", Цена=" + monthlyCost + " руб/мес]";
    }
}
