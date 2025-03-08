package controller;

<<<<<<< HEAD
import model.Analytics;
import database.DatabaseManager;
import java.time.LocalDate;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class AnalyticsController {
    private Analytics analytics;
    private int userId;
    private static final int DEFAULT_DAYS_RANGE = 30; // Show last 30 days by default

    public AnalyticsController() {
        this.analytics = new Analytics();
        this.userId = 1; // Default user ID, should be set properly
        loadAnalyticsData();
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadAnalyticsData();
    }

    private void loadAnalyticsData() {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Get date range
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(DEFAULT_DAYS_RANGE);

            // Load category spending with date range
            String categoryQuery =
                    "SELECT category, SUM(amount) as total " +
                            "FROM transactions " +
                            "WHERE user_id = ? AND date >= ? AND date <= ? " +
                            "GROUP BY category " +
                            "ORDER BY total DESC " +
                            "LIMIT 10"; // Show top 10 categories

            try (PreparedStatement stmt = conn.prepareStatement(categoryQuery)) {
                stmt.setInt(1, userId);
                stmt.setDate(2, java.sql.Date.valueOf(startDate));
                stmt.setDate(3, java.sql.Date.valueOf(endDate));
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String category = rs.getString("category");
                    double amount = rs.getDouble("total");
                    analytics.updateCategorySpending(category, amount);
                }
            }

            // Load daily spending with date range
            String dailyQuery =
                    "SELECT date, SUM(amount) as total " +
                            "FROM transactions " +
                            "WHERE user_id = ? AND date >= ? AND date <= ? " +
                            "GROUP BY date " +
                            "ORDER BY date DESC";

            try (PreparedStatement stmt = conn.prepareStatement(dailyQuery)) {
                stmt.setInt(1, userId);
                stmt.setDate(2, java.sql.Date.valueOf(startDate));
                stmt.setDate(3, java.sql.Date.valueOf(endDate));
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    LocalDate date = rs.getDate("date").toLocalDate();
                    double amount = rs.getDouble("total");
                    analytics.updateDailySpending(date, amount);
                }
            }

            // Load total budget (this doesn't need a date range as it's current budget)
            String budgetQuery = "SELECT total_budget FROM user_budget WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(budgetQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    analytics.setTotalBudget(rs.getDouble("total_budget"));
                }
            }

            // Load total spent for the current period
            String totalSpentQuery =
                    "SELECT SUM(amount) as total_spent " +
                            "FROM transactions " +
                            "WHERE user_id = ? AND date >= ? AND date <= ?";
            try (PreparedStatement stmt = conn.prepareStatement(totalSpentQuery)) {
                stmt.setInt(1, userId);
                stmt.setDate(2, java.sql.Date.valueOf(startDate));
                stmt.setDate(3, java.sql.Date.valueOf(endDate));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    double totalSpent = rs.getDouble("total_spent");
                    analytics.setTotalSpent(totalSpent);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> getCategorySpending() {
        return analytics.getCategorySpending();
    }

    public Map<LocalDate, Double> getDailySpending() {
        return analytics.getDailySpending();
    }

    public double getTotalBudget() {
        return analytics.getTotalBudget();
    }

    public double getTotalSpent() {
        return analytics.getTotalSpent();
    }

    public double getRemainingBudget() {
        return analytics.getRemainingBudget();
    }

    public double getSpendingPercentage() {
        return analytics.getSpendingPercentage();
    }

    public void importCSVFile(File file) throws IOException, SQLException {
        List<String[]> transactions = analytics.readCSVFile(file);

        try (Connection conn = DatabaseManager.getConnection()) {
            // Prepare SQL statement for inserting transactions
            String insertQuery =
                    "INSERT INTO transactions (user_id, date, amount, category, description, type) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                int batchSize = 0;
                for (String[] transaction : transactions) {
                    stmt.setInt(1, userId);
                    stmt.setDate(2, java.sql.Date.valueOf(transaction[0].trim())); // Date
                    stmt.setDouble(3, Double.parseDouble(transaction[1].trim().replace("$", "").replace(",", ""))); // Amount
                    stmt.setString(4, transaction[2]); // Category
                    stmt.setString(5, transaction[3]); // Description

                    // Determine if it's income or expense based on category
                    String type = transaction[2].equals("Income") ? "income" : "expense";
                    stmt.setString(6, type);

                    stmt.addBatch();
                    batchSize++;

                    // Execute batch every 100 records
                    if (batchSize % 100 == 0) {
                        stmt.executeBatch();
                        stmt.clearBatch();
                    }
                }

                // Execute remaining records
                if (batchSize % 100 != 0) {
                    stmt.executeBatch();
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

        // Reload analytics data after import
        loadAnalyticsData();
    }
}
=======
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
        connection = DatabaseManager.getConnection();
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
>>>>>>> refs/remotes/origin/Peyton
