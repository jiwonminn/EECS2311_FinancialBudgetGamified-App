package controller;

import database.DatabaseManager;
import model.Budget;
import utils.EmailNotifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class BudgetController {
    private Budget budget;
    private int userId;

    /**
     * Constructs a BudgetController for a given user.
     * The budget object holds preset limits (e.g., default values or user-configured limits).
     */
    public BudgetController(int userId, Budget budget) {
        this.userId = userId;
        this.budget = budget;
    }

    /**
     * Calculates the total expense for the current month for the user.
     * Assumes that transactions with type 'Expense' (case-insensitive) are expenses.
     */
    public double getMonthlySpending() {
        double total = 0.0;
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);
        String query = "SELECT SUM(amount) FROM transactions " +
                "WHERE user_id = ? AND LOWER(type) = 'expense' " +
                "AND date >= ? AND date < ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, java.sql.Date.valueOf(start));
            pstmt.setDate(3, java.sql.Date.valueOf(end));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * Calculates the total expense for the current week for the user.
     * Assumes week starts on Monday.
     */
    public double getWeeklySpending() {
        double total = 0.0;
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDate end = start.plusWeeks(1);
        String query = "SELECT SUM(amount) FROM transactions " +
                "WHERE user_id = ? AND LOWER(type) = 'expense' " +
                "AND date >= ? AND date < ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, java.sql.Date.valueOf(start));
            pstmt.setDate(3, java.sql.Date.valueOf(end));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * Returns the current balance calculated from transactions.
     * It sums all income transactions and subtracts all expense transactions.
     */
    public static double getCurrentBalance(int user) {
        double incomeTotal = 0.0;
        double expenseTotal = 0.0;
        String query = "SELECT type, SUM(amount) as total FROM transactions " +
                "WHERE user_id = ? GROUP BY type";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, user);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    double total = rs.getDouble("total");
                    if (type != null && type.equalsIgnoreCase("Income")) {
                        incomeTotal += total;
                    } else {
                        expenseTotal += total;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomeTotal - expenseTotal;
    }

    public boolean isOverWeeklyBudget() {
        return getWeeklySpending() > budget.getWeeklyLimit();
    }

    public boolean isOverMonthlyBudget() {
        return getMonthlySpending() > budget.getMonthlyLimit();
    }

    public void checkBudgetAndNotify(String recipientEmail, String username) {
        double monthlySpending = getMonthlySpending();
        double weeklySpending = getWeeklySpending();
        boolean weeklyExceeded = isOverWeeklyBudget();
        boolean monthlyExceeded = isOverMonthlyBudget();
        
        // Check for quest completion after budget checks
        try {
            QuestController questController = new QuestController();
            questController.checkAndCompleteQuests(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to check quests during budget verification!");
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
        // With no separate table, update only the in-memory model
    }

    public Budget getBudget() {
        return budget;
    }
}
