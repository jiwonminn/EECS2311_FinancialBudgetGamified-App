package database.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import model.Transaction;

public interface TransactionDao {
    List<Transaction> getTransactionsByUserId(int userId) throws SQLException;
    void addTransaction(int userId, LocalDate date, double amount, String description, String type, String category) throws SQLException;
    boolean updateTransaction(Transaction transaction) throws SQLException;
    boolean deleteTransaction(int transactionId) throws SQLException;
    int getTransactionCountForUser(int userId) throws SQLException;
    int getTransactionCountForDay(int userId, LocalDate date) throws SQLException;
}

