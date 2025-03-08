package controller;

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