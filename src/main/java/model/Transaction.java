package model;

import java.time.LocalDate;

public class Transaction {
    private String catalog;
    private double amount;
    private LocalDate date;
    private boolean isIncome; // true = income, false = expense

    public Transaction(String catalog, double amount, LocalDate date, boolean isIncome) {
        this.catalog = catalog;
        this.amount = amount;
        this.date = date;
        this.isIncome = isIncome;
    }

    public String getCatalog() {
        return catalog;
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
        return date + " | " + getType() + ": " + catalog + " - $" + amount;
    }
}
