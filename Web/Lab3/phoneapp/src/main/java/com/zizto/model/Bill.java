package com.zizto.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Bill {
    private int id;
    private int subscriberId;
    private BigDecimal amount;
    private Date issueDate;
    private boolean isPaid;

    // Геттеры, сеттеры и toString()
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSubscriberId() { return subscriberId; }
    public void setSubscriberId(int subscriberId) { this.subscriberId = subscriberId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean isPaid) { this.isPaid = isPaid; }

    @Override
    public String toString() {
        return "Счет [ID=" + id + ", ID абонента=" + subscriberId + ", Сумма=" + amount + ", Дата=" + issueDate + ", Оплачен=" + isPaid + "]";
    }
}
