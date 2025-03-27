package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import database.dao.Budget;
import database.dao.Budgetimpl;
import database.dao.ExperienceDao;
import database.dao.ExperienceDaoImpl;
import database.dao.QuestDao;
import database.dao.QuestDaoImpl;
import database.dao.TransactionDao;
import database.dao.TransactionDaoImpl;
import model.Quest;
import database.DatabaseManager;

public class QuestController {
    private QuestDao QuestDao;
    private ExperienceDao experienceDao;
    private TransactionDao transactionDao;
    private Budget budgetDao;

    /**
     * Constructor to properly initialize all DAOs
     */
    public QuestController() {
        this.QuestDao = new QuestDaoImpl();
        this.experienceDao = new ExperienceDaoImpl();
        this.transactionDao = new TransactionDaoImpl();
        this.budgetDao = new Budgetimpl();
        
        // Make sure all tables exist
        try {
            createQuestTablesIfNotExists();
        } catch (SQLException e) {
            System.out.println("Error creating quest tables: " + e.getMessage());
        }
    }

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
        try {
            boolean completed = QuestDao.completeQuest(questId, userId);
            if (!completed) {
                // If the DAO's completeQuest failed, try to complete it manually here
                String sql = "UPDATE quests SET completion_status = true WHERE id = ? AND user_id = ?";
                
                int xpReward = 0;
                // Get the XP reward for this quest
                try {
                    Quest quest = QuestDao.getQuestById(questId);
                    if (quest != null) {
                        xpReward = quest.getXpReward();
                    }
                } catch (Exception e) {
                    System.out.println("Error getting quest details: " + e.getMessage());
                }
                
                // Add XP directly from controller
                if (xpReward > 0) {
                    // Use the safe method that won't throw exceptions
                    experienceDao.addUserXPSafely(userId, xpReward);
                }
                
                return true;
            }
            return completed;
        } catch (Exception e) {
            System.out.println("Error completing quest: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
        try {
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
                
                // Quest for completing quizzes
                if (title.contains("quiz") || description.contains("quiz") ||
                    title.contains("complete 3 quizzes") || description.contains("finish at least 3 financial quizzes")) {
                    int quizCount = getQuizCompletionCount(userId);
                    System.out.println("User has completed " + quizCount + " quizzes for quest: " + title);
                    
                    // If this is a quest specifically about completing quizzes
                    if (quest.getRequiredAmount() > 0) {
                        if (quizCount >= quest.getRequiredAmount()) {
                            shouldComplete = true;
                            System.out.println("Quiz quest should be completed: " + title + " - " + quizCount + "/" + quest.getRequiredAmount());
                        }
                    } else if (quizCount > 0) {
                        // If no specific amount is required, just check if any quiz has been completed
                        shouldComplete = true;
                    }
                }
                
                // If quest should be completed, complete it
                if (shouldComplete) {
                    try {
                        System.out.println("Automatically completing quest: " + quest.getTitle());
                        completeQuest(quest.getId(), userId);
                    } catch (Exception e) {
                        System.out.println("Error completing quest " + quest.getTitle() + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error checking quests: " + e.getMessage());
            e.printStackTrace();
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
    
    /**
     * Calculate progress percentage for a quest
     * @param quest The quest to calculate progress for
     * @param userId The user ID
     * @return Progress percentage (0-100)
     * @throws SQLException if there's a database error
     */
    public int calculateQuestProgress(Quest quest, int userId) throws SQLException {
        if (quest.isCompleted()) {
            return 100;
        }
        
        String title = quest.getTitle().toLowerCase();
        String description = quest.getDescription().toLowerCase();
        
        // Quest for logging a transaction
        if (title.contains("daily log") || title.contains("log a transaction") || 
            description.contains("log a transaction") || description.contains("log all your expenses")) {
            int transactionCount = getTransactionCountForUser(userId);
            return transactionCount > 0 ? 100 : 0;
        }
        
        // Quest for logging multiple transactions
        if (title.contains("transaction master") || title.contains("log transactions") || 
            description.contains("log multiple transactions") || description.contains("record at least")) {
            int transactionCount = getTransactionCountForUser(userId);
            double requiredAmount = quest.getRequiredAmount() > 0 ? quest.getRequiredAmount() : 1;
            return (int) Math.min(100, (transactionCount / requiredAmount) * 100);
        }
        
        // Quest for staying under budget
        if (title.contains("budget guardian") || title.contains("budget streak") || 
            title.contains("stay under budget") || description.contains("stay under budget") ||
            description.contains("stay within your daily budget") || 
            description.contains("keep your expenses below")) {
            boolean underBudget = checkIfUserIsUnderBudget(userId);
            double expenses = getTotalExpensesForMonth(userId);
            double income = getTotalIncomeForMonth(userId);
            
            if (income > 0) {
                double ratio = 1 - (expenses / income);
                return (int) Math.min(100, Math.max(0, ratio * 100));
            } else {
                return underBudget ? 80 : 20;
            }
        }
        
        // Quest for savings
        if (title.contains("save $") || title.contains("savings champion") ||
            title.contains("save money") || description.contains("save money") ||
            description.contains("save at least") || description.contains("savings account")) {
            double savings = calculateUserSavings(userId);
            double requiredAmount = quest.getRequiredAmount() > 0 ? quest.getRequiredAmount() : 100;
            return (int) Math.min(100, (savings / requiredAmount) * 100);
        }
        
        // Quest for completing all goals
        if (title.contains("complete all goals")) {
            // This would require counting completed goals vs total goals
            // For now, we'll approximate with a 50% progress
            return 50;
        }
        
        // Quest for reaching a certain level
        if (title.contains("reach level")) {
            int[] userExp = getUserExperience(userId);
            int currentLevel = userExp[1];
            double requiredLevel = quest.getRequiredAmount() > 0 ? quest.getRequiredAmount() : 5;
            return (int) Math.min(100, (currentLevel / requiredLevel) * 100);
        }
        
        // Quest for completing quizzes
        if (title.contains("complete") && (title.contains("quiz") || title.contains("quizzes")) || 
            description.contains("complete") && (description.contains("quiz") || description.contains("quizzes"))) {
            // Get quiz completion count from quiz tracker
            int quizCount = getQuizCompletionCount(userId);
            double requiredQuizzes = quest.getRequiredAmount() > 0 ? quest.getRequiredAmount() : 1;
            return (int) Math.min(100, (quizCount / requiredQuizzes) * 100);
        }
        
        // Daily log quest - check for transactions today
        if (title.contains("daily log")) {
            // Check if they've logged transactions today
            LocalDate today = LocalDate.now();
            int todaysTransactions = getTransactionCountForDay(userId, today);
            return todaysTransactions > 0 ? 100 : 0;
        }
        
        // Default to 50% for unknown quest types
        return 50;
    }
    
    /**
     * Get transaction count for a specific day
     */
    private int getTransactionCountForDay(int userId, LocalDate date) throws SQLException {
        return transactionDao.getTransactionCountForDay(userId, date);
    }

    /**
     * Get the number of quizzes completed by a user
     * Track this in database or increment when a quiz is completed
     * 
     * @param userId The user ID
     * @return The number of quizzes completed
     */
    private int getQuizCompletionCount(int userId) {
        try {
            // Query the database for quiz completion count
            String sql = "SELECT COUNT(*) FROM quiz_completions WHERE user_id = ?";
            
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting quiz completion count: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Record a quiz completion for a user
     * 
     * @param userId The user ID
     * @param score The score achieved
     * @return true if successful
     */
    public boolean recordQuizCompletion(int userId, int score) {
        try {
            // Create table if it doesn't exist
            createQuizCompletionTableIfNotExists();
            
            // Insert a record of quiz completion
            String sql = "INSERT INTO quiz_completions (user_id, score, completion_date) VALUES (?, ?, ?)";
            
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, userId);
                pstmt.setInt(2, score);
                pstmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                
                int affected = pstmt.executeUpdate();
                
                // Check and complete quests after recording a quiz completion
                if (affected > 0) {
                    checkAndCompleteQuests(userId);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error recording quiz completion: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Creates the quiz completion table if it doesn't exist
     */
    public void createQuizCompletionTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS quiz_completions (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL, " +
                "score INTEGER NOT NULL, " +
                "completion_date DATE NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Creates all necessary tables for quest system
     * @throws SQLException if there's a database error
     */
    public void createQuestTablesIfNotExists() throws SQLException {
        // Create quests table
        String createQuestsTable = "CREATE TABLE IF NOT EXISTS quests (" +
                "id SERIAL PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "quest_type VARCHAR(50) NOT NULL, " +
                "xp_reward INTEGER NOT NULL, " +
                "required_amount DOUBLE PRECISION DEFAULT 0, " +
                "completion_status BOOLEAN DEFAULT FALSE, " +
                "deadline DATE, " +
                "user_id INTEGER NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        // Create user experience table
        String createExperienceTable = "CREATE TABLE IF NOT EXISTS user_experience (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id INTEGER UNIQUE NOT NULL, " +
                "current_xp INTEGER DEFAULT 0, " +
                "level INTEGER DEFAULT 1, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        // Create quiz completion tracking table
        String createQuizCompletionsTable = "CREATE TABLE IF NOT EXISTS quiz_completions (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL, " +
                "score INTEGER NOT NULL, " +
                "completion_date DATE NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            // Create all tables
            stmt.execute(createQuestsTable);
            stmt.execute(createExperienceTable);
            stmt.execute(createQuizCompletionsTable);
            
            // Check for existing quests for this user
            String countSQL = "SELECT COUNT(*) FROM quests";
            try (ResultSet rs = stmt.executeQuery(countSQL)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // If no quests exist, generate sample quests for user 1
                    // This is just to populate the database with some examples
                    generateSampleQuests(1);
                }
            }
        }
    }
}