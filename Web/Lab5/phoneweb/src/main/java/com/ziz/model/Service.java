    package com.ziz.model;

    import jakarta.persistence.*;
    import java.math.BigDecimal;

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

        public Service() {}
        
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

