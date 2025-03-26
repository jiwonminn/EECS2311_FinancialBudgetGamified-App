package controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager;
import database.dao.*;
import model.Quest;

public class QuestController {
    private QuestDao QuestDao = new QuestDaoImpl();
    private ExperienceDao experienceDao = new ExperienceDaoImpl();
    private TransactionDao transactionDao = new TransactionDaoImpl();
    private Budget budgetDao = new Budgetimpl();

    /**
     * Creates a new quest in the database
     */

    public int createQuest(Quest quest) throws SQLException {
        return QuestDao.createQuest(quest);
    }

    /**
     * Updates an existing quest in the database
     */
    public boolean updateQuest(Quest quest) throws SQLException {
        return QuestDao.updateQuest(quest);
    }

    /**
     * Deletes a quest from the database
     */
    public boolean deleteQuest(int questId, int userId) throws SQLException {
        return QuestDao.deleteQuest(questId, userId);
    }

    /**
     * Gets all quests for a specific user
     */
    public List<Quest> getQuestsByUserId(int userId) throws SQLException {
        return QuestDao.getQuestsByUserId(userId);
    }

    /**
     * Gets daily quests for a specific user
     */
    public List<Quest> getDailyQuestsByUserId(int userId) throws SQLException {
        return QuestDao.getDailyQuestsByUserId(userId);
    }

    /**
     * Gets weekly quests for a specific user
     */
    public List<Quest> getWeeklyQuestsByUserId(int userId) throws SQLException {
        return QuestDao.getWeeklyQuestsByUserId(userId);
    }

    /**
     * Gets monthly quests for a specific user
     */
    public List<Quest> getMonthlyQuestsByUserId(int userId) throws SQLException {
       return QuestDao.getMonthlyQuestsByUserId(userId);
    }

    /**
     * Completes a quest and adds XP to the user
     */
    public boolean completeQuest(int questId, int userId) throws SQLException {
        return QuestDao.completeQuest(questId, userId);
    }

    /**
     * Adds XP to a user and updates their level if necessary
     */
    public boolean addUserXP(int userId, int xpAmount) throws SQLException {
        return experienceDao.addUserXP(userId, xpAmount);
    }

    /**
     * Gets a user's current XP and level
     */
    public int[] getUserExperience(int userId) throws SQLException {
        return experienceDao.getUserExperience(userId);
    }

    /**
     * Calculates the level based on XP
     * Starting from level 0, with each level requiring more XP
     */
    private int calculateLevel(int xp) {
        // Modified to start at level 0 instead of level 1
        return (int) Math.floor(Math.sqrt(xp / 100.0));
    }

    /**
     * Gets XP required for the next level
     */
    public int getXpForNextLevel(int currentLevel) {
        // Adjusted formula for starting at level 0
        return ((currentLevel + 1) * (currentLevel + 1)) * 100;
    }

    /**
     * Maps a result set to a Quest object
     */
    private Quest mapResultSetToQuest(ResultSet rs) throws SQLException {
        return QuestDao.mapResultSetToQuest(rs);
    }

    /**
     * Generate sample quests for a user
     */
    public void generateSampleQuests(int userId) throws SQLException {
        List<Quest> sampleQuests = new ArrayList<>();

        // Daily quests
        sampleQuests.add(new Quest(0, "Daily Log Quest", "Log all your expenses for today", "DAILY", 20, 1, false, LocalDate.now().plusDays(1), userId));
        sampleQuests.add(new Quest(0, "Budget Guardian", "Stay within your daily budget", "DAILY", 30, 0, false, LocalDate.now().plusDays(1), userId));
        sampleQuests.add(new Quest(0, "Transaction Master", "Record at least 3 transactions", "DAILY", 25, 3, false, LocalDate.now().plusDays(1), userId));

        // Weekly quests
        sampleQuests.add(new Quest(0, "Save $50", "Save at least $50 this week", "WEEKLY", 100, 50, false, LocalDate.now().plusDays(7), userId));
        sampleQuests.add(new Quest(0, "Complete 3 quizzes", "Finish at least 3 financial quizzes", "WEEKLY", 75, 3, false, LocalDate.now().plusDays(7), userId));
        sampleQuests.add(new Quest(0, "Budget Streak", "Stay under budget for 5 consecutive days", "WEEKLY", 150, 5, false, LocalDate.now().plusDays(7), userId));

        // Monthly/Special quests
        sampleQuests.add(new Quest(0, "Complete all goals", "Complete all your financial goals for the month", "MONTHLY", 200, 0, false, LocalDate.now().plusMonths(1), userId));
        sampleQuests.add(new Quest(0, "Savings Champion", "Add to your savings account at least once a week", "MONTHLY", 300, 4, false, LocalDate.now().plusMonths(1), userId));
        sampleQuests.add(new Quest(0, "Reach level 5", "Reach user level 5 by completing quests", "MONTHLY", 500, 5, false, LocalDate.now().plusMonths(1), userId));

        // Add all sample quests to the database
        for (Quest quest : sampleQuests) {
            createQuest(quest);
        }

        // Add some initial XP so user starts at level 2
        addUserXP(userId, 150);
    }

    /**
     * Check and automatically complete quests based on user activities
     * @param userId The user ID to check quests for
     * @throws SQLException if there's a database error
     */
    public void checkAndCompleteQuests(int userId) throws SQLException {
        // Get all active quests for the user
        List<Quest> activeQuests = getActiveQuestsByUserId(userId);

        // For each quest, check if it should be completed
        for (Quest quest : activeQuests) {
            boolean shouldComplete = false;

            // Check quest completion based on quest title or description
            String title = quest.getTitle().toLowerCase();
            String description = quest.getDescription().toLowerCase();

            // Get user transaction count for transaction-related quests
            int transactionCount = getTransactionCountForUser(userId);

            // Quest for logging a transaction
            if (title.contains("log a transaction") || description.contains("log a transaction") ||
                    description.contains("log at least one transaction")) {
                if (transactionCount > 0) {
                    shouldComplete = true;
                }
            }

            // Quest for logging multiple transactions
            if (title.contains("log transactions") || description.contains("log multiple transactions")) {
                // Check if they've met the required amount (usually the number of transactions)
                if (transactionCount >= quest.getRequiredAmount()) {
                    shouldComplete = true;
                }
            }

            // Quest for staying under budget
            if (title.contains("stay under budget") || description.contains("stay under budget") ||
                    description.contains("keep your expenses below")) {
                // Calculate the user's budget status
                boolean underBudget = checkIfUserIsUnderBudget(userId);
                if (underBudget) {
                    shouldComplete = true;
                }
            }

            // Quest for savings
            if (title.contains("save money") || description.contains("save money") ||
                    title.contains("savings") || description.contains("savings")) {
                // Calculate the user's savings
                double savings = calculateUserSavings(userId);
                if (savings >= quest.getRequiredAmount()) {
                    shouldComplete = true;
                }
            }

            // If quest should be completed, complete it
            if (shouldComplete) {
                completeQuest(quest.getId(), userId);
                System.out.println("Automatically completed quest: " + quest.getTitle());
            }
        }
    }

    /**
     * Check if a user is under their budget
     */
    private boolean checkIfUserIsUnderBudget(int userId) throws SQLException {
        // Get total income and expenses for the current month
        double income = getTotalIncomeForMonth(userId);
        double expenses = getTotalExpensesForMonth(userId);

        // User is under budget if they've spent less than their income
        return expenses < income;
    }

    /**
     * Calculate a user's total savings (income - expenses)
     */
    private double calculateUserSavings(int userId) throws SQLException {
        double income = getTotalIncomeForMonth(userId);
        double expenses = getTotalExpensesForMonth(userId);

        return income - expenses;
    }

    /**
     * Get the total number of transactions for a user
     */
    private int getTransactionCountForUser(int userId) throws SQLException {
        return transactionDao.getTransactionCountForUser(userId);
    }

    /**
     * Get total income for the current month
     */
    private double getTotalIncomeForMonth(int userId) throws SQLException {
        return budgetDao.getTotalIncomeForMonth(userId);
    }

    /**
     * Get total expenses for the current month
     */
    private double getTotalExpensesForMonth(int userId) throws SQLException {
        return budgetDao.getTotalExpensesForMonth(userId);
    }

    /**
     * Gets all active (non-completed) quests for a user
     * @param userId The ID of the user
     * @return A list of active goals
     */
    public List<Quest> getActiveQuestsByUserId(int userId) throws SQLException {
        return QuestDao.getActiveQuestsByUserId(userId);
    }
}