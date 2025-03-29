package controller;

import model.User;
import utils.SessionManager;
import database.DatabaseManager;
import java.sql.*;

public class UserController {
    private User user;
    private int userId;

    public UserController(String username, String email, double balance) {
        this.user = new User(username, email, balance);
        // Default user ID, should be updated from database when available
        this.userId = 1;
    }
    
    /**
     * Constructor with user ID
     */
    public UserController(int userId, String username, String email, double balance) {
        this.user = new User(username, email, balance);
        this.userId = userId;
    }

    public void addPoints(int points) {
        user.addPoints(points);
    }

    public void updateBalance(double amount) {
        user.updateBalance(amount);
    }
    
    public int getPoints() {
        return user.getPoints();
    }
    
    /**
     * Get the user ID
     * @return the user ID
     */
    public int getUserId() {
        // Try to get from SessionManager first if ID is default
        if (userId == 1) {
            try {
                int sessionUserId = SessionManager.getInstance().getUserId();
                if (sessionUserId > 0) {
                    userId = sessionUserId;
                }
            } catch (Exception e) {
                // Silently fail, use default ID
            }
        }
        return userId;
    }
    
    /**
     * Set the user ID
     * @param userId the user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserInfo() {
        return "User: " + user.getUsername() + ", Email: "+ user.getEmail()+ ", Balance: " + user.getBalance() + ", Points: " + user.getPoints();
    }

    /**
     * Sets the monthly budget for a user
     * @param userId the user ID
     * @param budget the budget amount
     * @return true if successful, false otherwise
     */
    public boolean setMonthlyBudget(int userId, double budget) {
        String query = "UPDATE users SET monthly_budget = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, budget);
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the monthly budget for a user
     * @param userId the user ID
     * @return the monthly budget amount
     */
    public double getMonthlyBudget(int userId) {
        String query = "SELECT monthly_budget FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("monthly_budget");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Checks if the user has exceeded their monthly budget
     * @param userId the user ID
     * @return true if budget is exceeded, false otherwise
     */
    public boolean isBudgetExceeded(int userId) {
        String query = "SELECT monthly_budget, (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM CURRENT_DATE)) as total_spent FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double budget = rs.getDouble("monthly_budget");
                    double spent = rs.getDouble("total_spent");
                    return spent > budget;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
