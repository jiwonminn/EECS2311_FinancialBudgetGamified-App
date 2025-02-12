package model;

import java.time.LocalDate;

public class Transaction {
    private String description;
    private double amount;
    private LocalDate date;
    private boolean isIncome; // true = income, false = expense

    public Transaction(String description, double amount, LocalDate date, boolean isIncome) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.isIncome = isIncome;
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

    public String getType() {
        return isIncome ? "Income" : "Expense";
    }

    @Override
    public String toString() {
        return date + " | " + getType() + ": " + description + " - $" + amount;
    }
}
