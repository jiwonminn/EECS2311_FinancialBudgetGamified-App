package model;

import java.time.LocalDate;

public class Transaction {
    private String description;
    private double amount;
    private LocalDate date;
    private boolean isIncome; // true = income, false = expense
    private String category; // Added category field

    public Transaction(String description, double amount, LocalDate date, boolean isIncome) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.isIncome = isIncome;
        this.category = "Other"; // Default category
    }
    
    // Constructor with category
    public Transaction(String description, double amount, LocalDate date, boolean isIncome, String category) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.isIncome = isIncome;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isIncome() {
        return isIncome;
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
        return date + " | " + getType() + ": " + description + " - $" + amount + " (" + category + ")";
    }
}
