package main.java.model;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private String description;
    private double amount;
    private LocalDate date;
    private boolean isIncome; // true = income, false = expense
    private String category;

    public Transaction(String description, double amount, LocalDate date, boolean isIncome) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.isIncome = isIncome;
        this.category = "Other"; // Default category
    }
    
    public Transaction(String description, double amount, LocalDate date, boolean isIncome, String category) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.isIncome = isIncome;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return isIncome ? "Income" : "Expense";
    }

    @Override
    public String toString() {
        String type = isIncome ? "Income" : "Expense";
        return String.format("%s - $%.2f - %s - %s", date, amount, type, description);
    }
}
