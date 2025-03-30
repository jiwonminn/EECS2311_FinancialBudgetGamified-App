package controller;

import database.dao.TransactionDao;
import database.dao.TransactionDaoImpl;
import model.Transaction;
import model.Analytics;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;

public class AnalyticsController {
    private int userId;
    private TransactionDao transactionDao;

    public AnalyticsController(int userId) {
        this.userId = userId;
        this.transactionDao = new TransactionDaoImpl();
    }

    public List<Transaction> getTransactions() throws SQLException {
        return transactionDao.getTransactionsByUserId(userId);
    }

    public Map<LocalDate, Double> getIncomeData() throws SQLException {
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

    public Map<LocalDate, Double> getExpenseData() throws SQLException {
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

    public double getTotalIncome() throws SQLException {
        return getTransactions().stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense() throws SQLException {
        return getTransactions().stream()
                .filter(t -> !t.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getCurrentSavings() throws SQLException {
        return getTotalIncome() - getTotalExpense();
    }

    public Map<LocalDate, Double> getSavingsOverTime() throws SQLException {
        Map<LocalDate, Double> savingsMap = new HashMap<>();
        Map<LocalDate, Double> incomeMap = getIncomeData();
        Map<LocalDate, Double> expenseMap = getExpenseData();

        // Process dates with income
        for (LocalDate date : incomeMap.keySet()) {
            double income = incomeMap.get(date);
            double expense = expenseMap.getOrDefault(date, 0.0);
            savingsMap.put(date, income - expense);
        }
        // Process dates with only expense
        for (LocalDate date : expenseMap.keySet()) {
            if (!savingsMap.containsKey(date)) {
                savingsMap.put(date, -expenseMap.get(date));
            }
        }
        return savingsMap;
    }

    public Map<String, Double> getExpensesByCategory() throws SQLException {
        List<Transaction> transactions = getTransactions();
        Map<String, Double> expensesByCategory = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (!transaction.isIncome()) {
                String category = transaction.getCategory();
                expensesByCategory.put(category, expensesByCategory.getOrDefault(category, 0.0) + transaction.getAmount());
            }
        }
        return expensesByCategory;
    }

    public void addTransaction(LocalDate date, double amount, String description, String type, String category) throws SQLException {
        transactionDao.addTransaction(userId,  date, amount,  description,  type,  category);
    }

    /**
     * Imports transactions from a CSV file.
     * @param file The CSV file to import.
     * @throws IOException If there is an error reading the file.
     * @throws SQLException If there is an error adding transactions to the database.
     */
    public void importTransactionsFromCsv(File file) throws IOException, SQLException {
        Analytics analytics = new Analytics();
        List<String[]> transactions = analytics.readCSVFile(file);

        for (String[] transaction : transactions) {
            // Assuming the transaction array contains: [date, amount, category, description]
            LocalDate date = LocalDate.parse(transaction[0]); // Assuming date is in yyyy-MM-dd format
            double amount = Double.parseDouble(transaction[1]);
            String category = transaction[2];
            String description = transaction[3];

            // Determine if the transaction is income or expense
            String type = amount < 0 ? "expense" : "income"; // Assuming negative amounts are expenses

            // Add the transaction to the database
            addTransaction(date, amount, description, type, category);
        }
    }
}
