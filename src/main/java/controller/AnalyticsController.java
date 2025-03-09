package controller;

import database.DatabaseManager;
import model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsController {
    private int userId;

    public AnalyticsController(int userId) {
        this.userId = userId;
    }

    // Fetch all transactions for the given user, ordered by date DESC
    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Get category; default to "Other" if missing
                    String category;
                    try {
                        category = rs.getString("category");
                        if (category == null) {
                            category = "Other";
                        }
                    } catch (SQLException e) {
                        category = "Other";
                    }

                    // Retrieve the "type" column and convert to boolean:
                    // Assume "Income" (ignoring case) means true, else false.
                    String typeStr = rs.getString("type");
                    boolean isIncome = typeStr != null && typeStr.equalsIgnoreCase("Income");

                    // Convert SQL Date to LocalDate
                    LocalDate date = rs.getDate("date").toLocalDate();

                    Transaction transaction = new Transaction(
                            rs.getString("description"),
                            rs.getDouble("amount"),
                            date,
                            isIncome,
                            category
                    );
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to fetch transactions!");
        }
        return transactions;
    }

    // Aggregate income by date
    public Map<LocalDate, Double> getIncomeData() {
        List<Transaction> transactions = getTransactions();
        Map<LocalDate, Double> incomeMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (transaction.isIncome()) {
                LocalDate date = transaction.getDate();
                incomeMap.put(date, incomeMap.getOrDefault(date, 0.0) + transaction.getAmount());
            }
        }
        return incomeMap;
    }

    // Aggregate expenses by date
    public Map<LocalDate, Double> getExpenseData() {
        List<Transaction> transactions = getTransactions();
        Map<LocalDate, Double> expenseMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (!transaction.isIncome()) {
                LocalDate date = transaction.getDate();
                expenseMap.put(date, expenseMap.getOrDefault(date, 0.0) + transaction.getAmount());
            }
        }
        return expenseMap;
    }

    // Calculate total income
    public double getTotalIncome() {
        List<Transaction> transactions = getTransactions();
        return transactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Calculate total expense
    public double getTotalExpense() {
        List<Transaction> transactions = getTransactions();
        return transactions.stream()
                .filter(transaction -> !transaction.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Calculate current savings (income - expense)
    public double getCurrentSavings() {
        return getTotalIncome() - getTotalExpense();
    }

    // Track savings over time (e.g., daily savings)
    public Map<LocalDate, Double> getSavingsOverTime() {
        Map<LocalDate, Double> savingsMap = new HashMap<>();
        Map<LocalDate, Double> incomeMap = getIncomeData();
        Map<LocalDate, Double> expenseMap = getExpenseData();

        // Calculate savings for each date with income data
        for (LocalDate date : incomeMap.keySet()) {
            double income = incomeMap.getOrDefault(date, 0.0);
            double expense = expenseMap.getOrDefault(date, 0.0);
            savingsMap.put(date, income - expense);
        }
        // Include dates with only expenses
        for (LocalDate date : expenseMap.keySet()) {
            if (!savingsMap.containsKey(date)) {
                double expense = expenseMap.get(date);
                savingsMap.put(date, -expense);
            }
        }
        return savingsMap;
    }

    // Calculate total expenses by category
    public Map<String, Double> getExpensesByCategory() {
        List<Transaction> transactions = getTransactions();
        Map<String, Double> expensesByCategory = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (!transaction.isIncome()) {
                String category = transaction.getCategory();
                expensesByCategory.put(category, expensesByCategory.getOrDefault(category, 0.0) + transaction.getAmount());
            }
        }
        return expensesByCategory;
    }
}
