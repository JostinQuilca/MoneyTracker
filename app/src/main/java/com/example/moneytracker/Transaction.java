package com.example.moneytracker;

import java.io.Serializable;

public class Transaction implements Serializable {
    private int id;
    private String type;
    private double amount;
    private String category;
    private String description;
    private long date;
    private String paymentMethod; // NUEVO CAMPO REQUERIDO

    public Transaction() {}

    public Transaction(String type, double amount, String category, String description, long date, String paymentMethod) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
        this.paymentMethod = paymentMethod;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}