package controller;

import database.DatabaseManager;
import model.Transaction;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionController {
    private Connection connection;

    public TransactionController() throws SQLException {
        connection = DatabaseManager.getConnection();
    }

    /**
     * Adds a new transaction for the given user.
     *
     * @param userId      the user ID
     * @param date        the date in ISO format (YYYY-MM-DD)
     * @param description a description of the transaction
     * @param category    the transaction category
     * @param type        the transaction type (e.g., "income" or "expense")
     * @param amount      the transaction amount
     * @return true if the transaction was successfully added, false otherwise
     */
    public static boolean addTransactionk(int userId, String date, String description, String category, String type, double amount) {
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

    // ADD: Add Transaction without category (for backwards compatibility)
//    public void addTransaction(String description, double amount, LocalDate date, boolean isIncome) {
//        addTransaction(description, amount, date, isIncome, "Other");
//    }
    
    // ADD: Add Transaction with category
    public void addTransaction(String description, double amount, LocalDate date, boolean isIncome, String category) {
        String query = "INSERT INTO transactions (description, amount, date, is_income, category) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, description);
            stmt.setDouble(2, amount);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setBoolean(4, isIncome);
            stmt.setString(5, category);
            stmt.executeUpdate();
            System.out.println("Transaction added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add transaction!");
        }
    }

    // READ: Fetch all transactions
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

    // DELETE: Remove a transaction by ID
    public static boolean deleteTransaction(int transactionId) {
        String query = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, transactionId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * Retrieves all transactions for the given user from the database.
     *
     * @param userId the user ID
     * @return a List of Transaction objects
     */
    public static List<Transaction> getAllTransactions(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction(
                            rs.getInt("id"),
                            userId,
                            rs.getTimestamp("date"),
                            rs.getString("description"),
                            rs.getString("category"),
                            rs.getString("type"),
                            rs.getDouble("amount")
                    );
                    transactions.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

}
