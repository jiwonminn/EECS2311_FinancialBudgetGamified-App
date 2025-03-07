package model;

import java.time.LocalDate;

import controller.TransactionController;

public class Transaction extends TransactionController {
    private String description;
    private double amount;
    private LocalDate date;
    private boolean isIncome; // true = income, false = expense
    private String category; // Added category field

    public Transaction(String description, double amount, LocalDate date, boolean isIncome) {
    	if(amount < 0.00) {
    		throw new IllegalArgumentException("Amount can not be negative!");
    	}
    	else if(description == null) {
    		throw new IllegalArgumentException("Description can not be null");
    	}
    	else if(date == null) {
    		throw new IllegalArgumentException("Date can not be null");
    	}
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.isIncome = isIncome;
        this.category = "Other"; // Default category
    }
    
    // Constructor with category
    public Transaction(String description, double amount, LocalDate date, boolean isIncome, String category) {
    	if(amount < 0.00) {
    		throw new IllegalArgumentException("Amount can not be negative!");
    	}
    	if(amount < 0.00) {
    		throw new IllegalArgumentException("Amount can not be negative!");
    	}
    	else if(description == null) {
    		throw new IllegalArgumentException("Description can not be null");
    	}
    	else if(date == null) {
    		throw new IllegalArgumentException("Date can not be null");
    	}
    	else if(category == null) {
    		throw new IllegalArgumentException("Category can not be null");
    	}
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
