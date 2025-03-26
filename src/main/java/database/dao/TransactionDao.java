package database.dao;

import model.Transaction;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface TransactionDao {
    List<Transaction> getTransactionsByUserId(int userId) throws SQLException;
    void addTransaction(int userId, LocalDate date, double amount, String description, String type, String category) throws SQLException;
    boolean updateTransaction(Transaction transaction) throws SQLException;
    boolean deleteTransaction(int transactionId) throws SQLException;
    int getTransactionCountForUser(int userId) throws SQLException;
}

