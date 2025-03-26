package controller;

import database.dao.TransactionDao;
import database.dao.TransactionDaoImpl;
import model.Budget;
import model.Transaction;
import utils.EmailNotifier;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BudgetController {
    private Budget budget;
    private int userId;
    private TransactionDao transactionDao;

    public BudgetController(int userId, Budget budget) {
        this.userId = userId;
        this.budget = budget;
        this.transactionDao = new TransactionDaoImpl();
    }

    public double getMonthlySpending() throws SQLException {
        List<Transaction> transactions = transactionDao.getTransactionsByUserId(userId);
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);
        double total = 0.0;
        for (Transaction t : transactions) {
            if (!t.isIncome() && !t.getDate().isBefore(start) && t.getDate().isBefore(end)) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public double getWeeklySpending() throws SQLException {
        List<Transaction> transactions = transactionDao.getTransactionsByUserId(userId);
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDate end = start.plusWeeks(1);
        double total = 0.0;
        for (Transaction t : transactions) {
            if (!t.isIncome() && !t.getDate().isBefore(start) && t.getDate().isBefore(end)) {
                total += t.getAmount();
            }
        }
        return total;
    }

    // A static helper method that computes the balance using transactions.
    public static double getCurrentBalance(int userId) throws SQLException {
        TransactionDao transactionDao = new TransactionDaoImpl();
        List<Transaction> transactions = transactionDao.getTransactionsByUserId(userId);
        double incomeTotal = transactions.stream().filter(Transaction::isIncome).mapToDouble(Transaction::getAmount).sum();
        double expenseTotal = transactions.stream().filter(t -> !t.isIncome()).mapToDouble(Transaction::getAmount).sum();
        return incomeTotal - expenseTotal;
    }

    public boolean isOverWeeklyBudget() throws SQLException {
        return getWeeklySpending() > budget.getWeeklyLimit();
    }

    public boolean isOverMonthlyBudget() throws SQLException {
        return getMonthlySpending() > budget.getMonthlyLimit();
    }

    public void checkBudgetAndNotify(String recipientEmail, String username) throws SQLException {
        double monthlySpending = getMonthlySpending();
        double weeklySpending = getWeeklySpending();
        boolean weeklyExceeded = isOverWeeklyBudget();
        boolean monthlyExceeded = isOverMonthlyBudget();

        // Check for quest completion
        try {
            QuestController questController = new QuestController();
            questController.checkAndCompleteQuests(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (weeklyExceeded) {
            EmailNotifier.sendBudgetExceededEmail(recipientEmail, username, budget.getWeeklyLimit(), weeklySpending);
        }
        if (monthlyExceeded) {
            EmailNotifier.sendBudgetExceededEmail(recipientEmail, username, budget.getMonthlyLimit(), monthlySpending);
        }
    }

    public void updateBudget(double monthly, double weekly) {
        budget.setMonthlyLimit(monthly);
        budget.setWeeklyLimit(weekly);
        // Persist changes if needed
    }

    public Budget getBudget() {
        return budget;
    }
}
