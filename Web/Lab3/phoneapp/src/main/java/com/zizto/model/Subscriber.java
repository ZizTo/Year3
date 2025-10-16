package com.zizto.model;

public class Subscriber {
    private int id;
    private String fullName;
    private String phoneNumber;
    private boolean isBlocked;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean isBlocked) { this.isBlocked = isBlocked; }

    @Override
    public String toString() {
        return "Абонент [ID=" + id + ", ФИО=" + fullName + ", Телефон=" + phoneNumber + ", Заблокирован=" + isBlocked + "]";
    }
}
