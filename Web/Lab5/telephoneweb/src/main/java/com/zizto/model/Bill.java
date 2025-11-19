package com.zizto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "Bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscriber_id")
    private Subscriber subscriber;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "issue_date")
    private Date issueDate;

    @Column(name = "is_paid")
    private boolean isPaid;

    public Bill() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Subscriber getSubscriber() { return subscriber; }
    public void setSubscriber(Subscriber subscriber) { this.subscriber = subscriber; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean isPaid) { this.isPaid = isPaid; }

    @Override
    public String toString() {
        String subscriberName = (subscriber != null) ? subscriber.getFullName() : "N/A";
        return "Счет [ID=" + id + ", Абонент=" + subscriberName + ", Сумма=" + amount + ", Дата=" + issueDate + ", Оплачен=" + isPaid + "]";
    }
}
