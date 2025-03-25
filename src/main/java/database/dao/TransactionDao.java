package database.dao;

import model.Transaction;
import java.sql.SQLException;
import java.util.List;

public interface TransactionDao {
    List<Transaction> getTransactionsByUserId(int userId) throws SQLException;
    boolean addTransaction(Transaction transaction) throws SQLException;
    boolean updateTransaction(Transaction transaction) throws SQLException;
    boolean deleteTransaction(int transactionId) throws SQLException;
}

