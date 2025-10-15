package com.zizto.model;

import java.math.BigDecimal;

public class Service {
    private int id;
    private String name;
    private BigDecimal monthlyCost;
    
    // Геттеры, сеттеры и toString()
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getMonthlyCost() { return monthlyCost; }
    public void setMonthlyCost(BigDecimal monthlyCost) { this.monthlyCost = monthlyCost; }

    @Override
    public String toString() {
        return "Услуга [ID=" + id + ", Название=" + name + ", Цена=" + monthlyCost + " руб/мес]";
    }
}

