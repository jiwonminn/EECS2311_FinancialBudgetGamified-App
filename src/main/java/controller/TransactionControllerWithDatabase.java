package controller;

import database.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionControllerWithDatabase {
    public static boolean addTransaction(int userId, String date, String description, String category, String type, double amount) {
        String query = "INSERT INTO transactions (user_id, date, description, category, type, amount) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            pstmt.setString(3, description);
            pstmt.setString(4, category);
            pstmt.setString(5, type);
            pstmt.setDouble(6, amount);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void viewTransactions(int userId) {
        String query = "SELECT * FROM transactions WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("ID: %d | Date: %s | Description: %s | Category: %s | Type: %s | Amount: %.2f\n",
                            rs.getInt("id"), rs.getTimestamp("date"),
                            rs.getString("description"), rs.getString("category"),
                            rs.getString("type"), rs.getDouble("amount"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

