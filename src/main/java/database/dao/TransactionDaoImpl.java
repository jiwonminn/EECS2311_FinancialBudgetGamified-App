package database.dao;

import model.Transaction;
import database.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDaoImpl implements TransactionDao {

    @Override
    public List<Transaction> getTransactionsByUserId(int userId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String category = rs.getString("category");
                    if (category == null) {
                        category = "Other";
                    }
                    boolean isIncome = rs.getString("type") != null &&
                            rs.getString("type").equalsIgnoreCase("income");
                    LocalDate date = rs.getDate("date").toLocalDate();
                    Transaction transaction = new Transaction(
                            rs.getString("description"),
                            rs.getDouble("amount"),
                            date,
                            isIncome,
                            category
                    );
                    transaction.setId(rs.getInt("id"));
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }

    @Override
    public boolean addTransaction(Transaction transaction) throws SQLException {
        String query = "INSERT INTO transactions (user_id, date, description, category, type, amount) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, transaction.getUserId());
            pstmt.setDate(2, java.sql.Date.valueOf(transaction.getDate()));
            pstmt.setString(3, transaction.getDescription());
            pstmt.setString(4, transaction.getCategory());
            pstmt.setString(5, transaction.isIncome() ? "income" : "expense");
            pstmt.setDouble(6, transaction.getAmount());
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateTransaction(Transaction transaction) throws SQLException {
        String query = "UPDATE transactions SET date = ?, description = ?, category = ?, type = ?, amount = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, java.sql.Date.valueOf(transaction.getDate()));
            pstmt.setString(2, transaction.getDescription());
            pstmt.setString(3, transaction.getCategory());
            pstmt.setString(4, transaction.isIncome() ? "income" : "expense");
            pstmt.setDouble(5, transaction.getAmount());
            pstmt.setInt(6, transaction.getId());
            pstmt.setInt(7, transaction.getUserId());
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteTransaction(int transactionId) throws SQLException {
        String query = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, transactionId);
            return pstmt.executeUpdate() > 0;
        }
    }
}

