package controller;

import database.DatabaseManager;
import model.Transaction;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionController {
    private Connection connection;

    public TransactionController() {
        connection = DatabaseManager.getConnection();
    }

    // ADD: Add Transaction without category (for backwards compatibility)
    public void addTransaction(String description, double amount, LocalDate date, boolean isIncome) {
        addTransaction(description, amount, date, isIncome, "Other");
        if(amount < 0.00) {
    		throw new IllegalArgumentException("Amount can not be negative!");
    	}
    	else if(description == null) {
    		throw new IllegalArgumentException("Description can not be null");
    	}
    	else if(date == null) {
    		throw new IllegalArgumentException("Date can not be null");
    	}
    }
    
    // ADD: Add Transaction with category
    public void addTransaction(String description, double amount, LocalDate date, boolean isIncome, String category) {
    	if(amount < 0.00) {
    		throw new IllegalArgumentException("Amount can not be negative");
    	}
    	else if(description.length() <= 0) {
    		throw new IllegalArgumentException("Description can not be empty");
    	}
    	else if(description == null) {
    		throw new NullPointerException("Description can not be null");
    	}
    	else if(date == null) {
    		throw new NullPointerException("Date can not be null");
    	}
    	else if(category == null) {
    		throw new NullPointerException("Category can not be null");
    	}
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
    public void deleteTransaction(int id) {
        String query = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Transaction deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to delete transaction!");
        }
    }
}
