package model;

import java.sql.Timestamp;
import java.time.LocalDate;

public class Transaction {
    private int id;
    private int userId;
    private String description;
    private double amount;
    private LocalDate date;
    private boolean isIncome; // true = income, false = expense
    private String category;  // Transaction category

    // Existing constructors for creating transactions manually
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

    // New constructor for database retrieval
    // It accepts an id, a userId, a Timestamp for the date, a description,
    // a category, a type as a String ("income" or "expense"), and the amount.
    public Transaction(int id, int userId, Timestamp timestamp, String description, String category, String type, double amount) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.date = timestamp.toLocalDateTime().toLocalDate();
        this.category = category;
        this.isIncome = type.equalsIgnoreCase("income");
    }

    // Getters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
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

    // Returns "Income" or "Expense" based on isIncome
    public String getType() {
        return isIncome ? "Income" : "Expense";
    }

    @Override
    public String toString() {
        return date + " | " + getType() + ": " + description + " - $" + amount + " (" + category + ")";
    }
}
