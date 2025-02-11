package main.java.controller;

import main.java.model.Transaction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionController {
    private List<Transaction> transactions;

    public TransactionController() {
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(String description, double amount, LocalDate date, boolean isIncome) {
        transactions.add(new Transaction(description, amount, date, isIncome));
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Transaction> getTransactionsByDate(LocalDate date) {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getDate().equals(date)) {
                filtered.add(t);
            }
        }
        return filtered;
    }

    public double getTotalIncome() {
        return transactions.stream().filter(Transaction::isIncome).mapToDouble(Transaction::getAmount).sum();
    }

    public double getTotalExpenses() {
        return transactions.stream().filter(t -> !t.isIncome()).mapToDouble(Transaction::getAmount).sum();
    }
}
