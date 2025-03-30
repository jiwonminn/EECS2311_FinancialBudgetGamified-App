package controller;

import database.dao.GoalDao;
import database.dao.GoalDaoImpl;
import database.dao.TransactionDao;
import database.dao.TransactionDaoImpl;
import model.Goal;
import model.Transaction;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Date;

public class GoalController {
    private GoalDao goalDao;
    private TransactionDao transactionDao;

    public GoalController() {
        this.goalDao = new GoalDaoImpl();
        this.transactionDao = new TransactionDaoImpl();
    }

    public int createGoal(Goal goal) throws SQLException {
        if (isDuplicateGoalName(goal.getUserId(), goal.getTitle())) {
            throw new IllegalArgumentException("A goal with the same name already exists for this user.");
        }
        return goalDao.createGoal(goal);
    }

    public boolean updateGoal(Goal goal) throws SQLException {
        return goalDao.updateGoal(goal);
    }

    public boolean deleteGoal(int goalId) throws SQLException {
        return goalDao.deleteGoal(goalId);
    }

    public Goal getGoalById(int goalId) throws SQLException {
        return goalDao.getGoalById(goalId);
    }

    public List<Goal> getGoalsByUserId(int userId) throws SQLException {
        return goalDao.getGoalsByUserId(userId);
    }

    public List<Goal> getActiveGoalsByUserId(int userId) throws SQLException {
        return goalDao.getActiveGoalsByUserId(userId);
    }

    public boolean updateGoalProgress(int goalId) throws SQLException {
        Goal goal = getGoalById(goalId);
        if (goal == null) {
            return false;
        }
        List<Transaction> transactions = transactionDao.getTransactionsByUserId(goal.getUserId());
        double totalSavings = 0.0;
        for (Transaction transaction : transactions) {
            LocalDate txDate = transaction.getDate();
            LocalDate start = convertToLocalDate(goal.getStartDate());
            LocalDate end = convertToLocalDate(goal.getTargetDate());
            if (transaction.getCategory().equals(goal.getCategory()) &&
                    (!txDate.isBefore(start)) && (!txDate.isAfter(end)) &&
                    transaction.isIncome()) {
                totalSavings += transaction.getAmount();
            }
        }
        goal.setCurrentAmount(totalSavings);
        return updateGoal(goal);
    }

    private LocalDate convertToLocalDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

    private boolean isDuplicateGoalName(int userId, String goalName) throws SQLException {
        List<Goal> goals = goalDao.getGoalsByUserId(userId);
        for (Goal existingGoal : goals) {
            if (existingGoal.getTitle().equalsIgnoreCase(goalName)) {
                return true;
            }
        }
        return false;
    }
}
