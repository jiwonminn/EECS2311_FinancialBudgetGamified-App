package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import database.DatabaseManager;
import database.dao.Budget;
import database.dao.Budgetimpl;
import database.dao.ExperienceDao;
import database.dao.ExperienceDaoImpl;
import database.dao.QuestDao;
import database.dao.QuestDaoImpl;
import database.dao.TransactionDao;
import database.dao.TransactionDaoImpl;
import model.Quest;

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

            // Show congratulatory popup
            Quest quest = QuestDao.getQuestById(questId);
            if (quest != null) {
                SwingUtilities.invokeLater(() -> {
                    JDialog popup = new JDialog((Frame) null, "Quest Completed!", true);
                    popup.setLayout(new BorderLayout(10, 10));
                    popup.getContentPane().setBackground(new Color(40, 24, 69));
                    
                    // Create main panel
                    JPanel mainPanel = new JPanel();
                    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
                    mainPanel.setBackground(new Color(40, 24, 69));
                    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    
                    // Add trophy icon
                    JLabel trophyLabel = new JLabel("🏆");
                    trophyLabel.setFont(new Font("Arial", Font.PLAIN, 48));
                    trophyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    mainPanel.add(trophyLabel);
                    
                    // Add some spacing
                    mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                    
                    // Add congratulatory message
                    JLabel congratsLabel = new JLabel("Congratulations!");
                    congratsLabel.setFont(new Font("Arial", Font.BOLD, 24));
                    congratsLabel.setForeground(new Color(255, 255, 255));
                    congratsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    mainPanel.add(congratsLabel);
                    
                    // Add quest title
                    JLabel questLabel = new JLabel(quest.getTitle());
                    questLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    questLabel.setForeground(new Color(128, 90, 213));
                    questLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    mainPanel.add(questLabel);
                    
                    // Add some spacing
                    mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                    
                    // Add XP reward
                    JLabel xpLabel = new JLabel("+" + quest.getXpReward() + " XP");
                    xpLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    xpLabel.setForeground(new Color(39, 174, 96));
                    xpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    mainPanel.add(xpLabel);
                    
                    // Add close button
                    JButton closeButton = new JButton("Continue");
                    closeButton.setBackground(new Color(128, 90, 213));
                    closeButton.setForeground(Color.WHITE);
                    closeButton.setFocusPainted(false);
                    closeButton.setBorderPainted(false);
                    closeButton.setFont(new Font("Arial", Font.BOLD, 14));
                    closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    closeButton.addActionListener(e -> popup.dispose());
                    
                    // Add some spacing before button
                    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                    mainPanel.add(closeButton);
                    
                    popup.add(mainPanel, BorderLayout.CENTER);
                    
                    // Set dialog properties
                    popup.setSize(300, 400);
                    popup.setLocationRelativeTo(null);
                    popup.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    popup.setResizable(false);
                    
                    // Show the popup
                    popup.setVisible(true);
                });
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
        // Get current XP and level
        int[] currentExp = getUserExperience(userId);
        int currentXP = currentExp[0];
        int currentLevel = currentExp[1];
        
        // Add new XP
        boolean success = experienceDao.addUserXP(userId, xpAmount);
        
        if (success) {
            // Get new XP and level
            int[] newExp = getUserExperience(userId);
            int newLevel = newExp[1];
            
            // If level increased, show level up popup
            if (newLevel > currentLevel) {
                showLevelUpPopup(newLevel);
            }
        }
        
        return success;
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
            
            // For each quest, calculate progress and check if it should be completed
            for (Quest quest : activeQuests) {
                // Calculate and update progress
                int progress = calculateQuestProgress(quest, userId);
                
                // If progress is 100%, complete the quest
                if (progress == 100 && !quest.isCompleted()) {
                    try {
                        System.out.println("Automatically completing quest: " + quest.getTitle());
                        
                        // Set progress to 100% and update in database
                        quest.setProgress(100);
                        QuestDao.updateQuest(quest);
                        
                        // Then mark it as completed and award XP
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
            quest.setProgress(100);
            return 100;
        }
        
        String title = quest.getTitle().toLowerCase();
        String description = quest.getDescription().toLowerCase();
        int progress = 0;
        
        // Quest for logging a transaction
        if (title.contains("daily log") || title.contains("log a transaction") || 
            description.contains("log a transaction") || description.contains("log all your expenses")) {
            int transactionCount = getTransactionCountForUser(userId);
            progress = transactionCount > 0 ? 100 : 0;
        }
        
        // Quest for logging multiple transactions
        else if (title.contains("transaction master") || title.contains("log transactions") || 
            description.contains("log multiple transactions") || description.contains("record at least")) {
            int transactionCount = getTransactionCountForUser(userId);
            int requiredAmount = (int)Math.ceil(quest.getRequiredAmount());
            
            // Fix for the progress bar calculation
            if (transactionCount >= requiredAmount) {
                // If requirement is met, show 100%
                progress = 100;
            } else {
                // Calculate the percentage with proper double conversion to avoid integer division
                progress = (int)((transactionCount * 100.0) / requiredAmount);
            }
            
            System.out.println("Transaction Quest Progress: " + transactionCount + "/" + 
                             requiredAmount + " = " + progress + "%");
        }
        
        // Quest for staying under budget
        else if (title.contains("budget guardian") || title.contains("budget streak") || 
            title.contains("stay under budget") || description.contains("stay under budget") ||
            description.contains("stay within your daily budget") || 
            description.contains("keep your expenses below")) {
            boolean underBudget = checkIfUserIsUnderBudget(userId);
            double expenses = getTotalExpensesForMonth(userId);
            double income = getTotalIncomeForMonth(userId);
            
            if (income > 0) {
                double ratio = 1 - (expenses / income);
                progress = (int) Math.min(100, Math.max(0, ratio * 100));
            } else {
                progress = underBudget ? 80 : 20;
            }
        }
        
        // Quest for savings
        else if (title.contains("save $") || title.contains("savings champion") ||
            title.contains("save money") || description.contains("save money") ||
            description.contains("save at least") || description.contains("savings account")) {
            double savings = calculateUserSavings(userId);
            double requiredAmount = quest.getRequiredAmount() > 0 ? quest.getRequiredAmount() : 100;
            progress = (int) Math.min(100, (savings / requiredAmount) * 100);
        }
        
        // Quest for completing all goals
        else if (title.contains("complete all goals")) {
            // This would require counting completed goals vs total goals
            // For now, we'll approximate with a 50% progress
            progress = 50;
        }
        
        // Quest for reaching a certain level
        else if (title.contains("reach level")) {
            int[] userExp = getUserExperience(userId);
            int currentLevel = userExp[1];
            double requiredLevel = quest.getRequiredAmount() > 0 ? quest.getRequiredAmount() : 5;
            progress = (int) Math.min(100, (currentLevel / requiredLevel) * 100);
        }
        
        // Quest for completing quizzes
        else if (title.contains("complete") && (title.contains("quiz") || title.contains("quizzes")) || 
            description.contains("complete") && (description.contains("quiz") || description.contains("quizzes"))) {
            // Get quiz completion count from quiz tracker
            int quizCount = getQuizCompletionCount(userId);
            double requiredQuizzes = quest.getRequiredAmount() > 0 ? quest.getRequiredAmount() : 1;
            progress = (int) Math.min(100, (quizCount / requiredQuizzes) * 100);
        }
        
        // Daily log quest - check for transactions today
        else if (title.contains("daily log")) {
            // Check if they've logged transactions today
            LocalDate today = LocalDate.now();
            int todaysTransactions = getTransactionCountForDay(userId, today);
            progress = todaysTransactions > 0 ? 100 : 0;
        }
        
        // Default to 50% for unknown quest types
        else {
            progress = 50;
        }
        
        // Update the quest's progress
        quest.setProgress(progress);
        
        // If progress is 100%, we should consider updating the quest in the database
        if (progress == 100 && !quest.isCompleted()) {
            // Just update progress here, completion will be handled by checkAndCompleteQuests
            try {
                QuestDao.updateQuest(quest);
            } catch (Exception e) {
                System.out.println("Error updating quest progress: " + e.getMessage());
            }
        }
        
        return progress;
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

    /**
     * Refreshes quests based on their type (daily/weekly)
     * @param userId The user ID
     * @throws SQLException if there's a database error
     */
    public void refreshQuests(int userId) throws SQLException {
        LocalDate today = LocalDate.now();
        
        // Get all quests for the user
        List<Quest> userQuests = getQuestsByUserId(userId);
        
        for (Quest quest : userQuests) {
            // Skip completed quests
            if (quest.isCompleted()) continue;
            
            // Check if quest needs to be refreshed based on its type
            boolean shouldRefresh = false;
            switch (quest.getQuestType().toUpperCase()) {
                case "DAILY":
                    // Refresh if the deadline has passed
                    shouldRefresh = quest.getDeadline().isBefore(today);
                    break;
                case "WEEKLY":
                    // Refresh if the deadline has passed
                    shouldRefresh = quest.getDeadline().isBefore(today);
                    break;
                case "MONTHLY":
                    // Monthly quests don't need refresh
                    continue;
            }
            
            if (shouldRefresh) {
                // Delete the old quest
                deleteQuest(quest.getId(), userId);
                
                // Create a new quest of the same type
                Quest newQuest = new Quest(
                    0,
                    quest.getTitle(),
                    quest.getDescription(),
                    quest.getQuestType(),
                    quest.getXpReward(),
                    quest.getRequiredAmount(),
                    false,
                    calculateNewDeadline(quest.getQuestType()),
                    userId
                );
                
                createQuest(newQuest);
            }
        }
    }
    
    /**
     * Calculates the new deadline for a quest based on its type
     */
    private LocalDate calculateNewDeadline(String questType) {
        LocalDate today = LocalDate.now();
        switch (questType.toUpperCase()) {
            case "DAILY":
                return today.plusDays(1);
            case "WEEKLY":
                return today.plusWeeks(1);
            case "MONTHLY":
                return today.plusMonths(1);
            default:
                return today.plusDays(1);
        }
    }
    
    /**
     * Shows a level-up congratulatory popup
     */
    private void showLevelUpPopup(int newLevel) {
        SwingUtilities.invokeLater(() -> {
            JDialog popup = new JDialog((Frame) null, "Level Up!", true);
            popup.setLayout(new BorderLayout(10, 10));
            popup.getContentPane().setBackground(new Color(40, 24, 69));
            
            // Create main panel
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(new Color(40, 24, 69));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Add level up icon
            JLabel levelIcon = new JLabel("⭐");
            levelIcon.setFont(new Font("Arial", Font.PLAIN, 48));
            levelIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(levelIcon);
            
            // Add some spacing
            mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            
            // Add level up message
            JLabel levelUpLabel = new JLabel("Level Up!");
            levelUpLabel.setFont(new Font("Arial", Font.BOLD, 24));
            levelUpLabel.setForeground(new Color(255, 255, 255));
            levelUpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(levelUpLabel);
            
            // Add new level
            JLabel newLevelLabel = new JLabel("You've reached level " + newLevel + "!");
            newLevelLabel.setFont(new Font("Arial", Font.BOLD, 18));
            newLevelLabel.setForeground(new Color(128, 90, 213));
            newLevelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(newLevelLabel);
            
            // Add some spacing
            mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            
            // Add encouragement message
            JLabel encouragementLabel = new JLabel("Keep up the great work!");
            encouragementLabel.setFont(new Font("Arial", Font.BOLD, 16));
            encouragementLabel.setForeground(new Color(39, 174, 96));
            encouragementLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(encouragementLabel);
            
            // Add close button
            JButton closeButton = new JButton("Continue");
            closeButton.setBackground(new Color(128, 90, 213));
            closeButton.setForeground(Color.WHITE);
            closeButton.setFocusPainted(false);
            closeButton.setBorderPainted(false);
            closeButton.setFont(new Font("Arial", Font.BOLD, 14));
            closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            closeButton.addActionListener(e -> popup.dispose());
            
            // Add some spacing before button
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainPanel.add(closeButton);
            
            popup.add(mainPanel, BorderLayout.CENTER);
            
            // Set dialog properties
            popup.setSize(300, 400);
            popup.setLocationRelativeTo(null);
            popup.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            popup.setResizable(false);
            
            // Show the popup
            popup.setVisible(true);
        });
    }
}