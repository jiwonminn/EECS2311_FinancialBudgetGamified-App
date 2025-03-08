package controller;
import database.DatabaseManager;
import model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsController {
    private Connection connection;

    public AnalyticsController() {
        try {
			connection = DatabaseManager.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // Fetch all transactions
    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions ORDER BY date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String category = "Other";
                try {
                    category = rs.getString("category");
                    if (category == null) category = "Other";
                } catch (SQLException e) {
                    // Column might not exist in older database versions
                    category = "Other";
                }
                
                Transaction transaction = new Transaction(
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getDate("date").toLocalDate(),
                        rs.getBoolean("is_income"),
                        category
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to fetch transactions!");
        }
        return transactions;
    }

    // Aggregate income and expenses by date
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

    // Calculate total income and expenses
    public double getTotalIncome() {
        List<Transaction> transactions = getTransactions();
        return transactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense() {
        List<Transaction> transactions = getTransactions();
        return transactions.stream()
                .filter(transaction -> !transaction.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
 // Calculate current savings (total income - total expenses)
    public double getCurrentSavings() {
        return getTotalIncome() - getTotalExpense();
    }

    // Track savings over time (e.g., monthly savings)
    public Map<LocalDate, Double> getSavingsOverTime() {
        Map<LocalDate, Double> savingsMap = new HashMap<>();
        Map<LocalDate, Double> incomeMap = getIncomeData();
        Map<LocalDate, Double> expenseMap = getExpenseData();

        // Calculate savings for each date
        for (LocalDate date : incomeMap.keySet()) {
            double income = incomeMap.getOrDefault(date, 0.0);
            double expense = expenseMap.getOrDefault(date, 0.0);
            savingsMap.put(date, income - expense);
        }

        // Include dates with only expenses
        for (LocalDate date : expenseMap.keySet()) {
            if (!savingsMap.containsKey(date)) {
                double expense = expenseMap.get(date);
                savingsMap.put(date, -expense); // Negative savings for expense-only dates
            }
        }

        return savingsMap;
    }

 // Calculate total expenses by category
    public Map<String, Double> getExpensesByCategory() {
        List<Transaction> transactions = getTransactions();
        Map<String, Double> expensesByCategory = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (!transaction.isIncome()) { // Only consider expenses
                String category = transaction.getCategory();
                expensesByCategory.put(category, expensesByCategory.getOrDefault(category, 0.0) + transaction.getAmount());
            }
        }

        return expensesByCategory;
    }
}
