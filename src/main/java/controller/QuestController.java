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
import model.Quest;

public class QuestController {
    
    /**
     * Creates a new quest in the database
     */
    public int createQuest(Quest quest) throws SQLException {
        String sql = "INSERT INTO quests (title, description, quest_type, xp_reward, required_amount, completion_status, deadline, user_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, quest.getTitle());
            pstmt.setString(2, quest.getDescription());
            pstmt.setString(3, quest.getQuestType());
            pstmt.setInt(4, quest.getXpReward());
            pstmt.setDouble(5, quest.getRequiredAmount());
            pstmt.setBoolean(6, quest.isCompleted());
            if (quest.getDeadline() != null) {
                pstmt.setDate(7, java.sql.Date.valueOf(quest.getDeadline()));
            } else {
                pstmt.setNull(7, Types.DATE);
            }
            pstmt.setInt(8, quest.getUserId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        
        return -1; // Failed to create quest
    }
    
    /**
     * Updates an existing quest in the database
     */
    public boolean updateQuest(Quest quest) throws SQLException {
        String sql = "UPDATE quests SET title = ?, description = ?, quest_type = ?, " +
                     "xp_reward = ?, required_amount = ?, completion_status = ?, deadline = ? " +
                     "WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, quest.getTitle());
            pstmt.setString(2, quest.getDescription());
            pstmt.setString(3, quest.getQuestType());
            pstmt.setInt(4, quest.getXpReward());
            pstmt.setDouble(5, quest.getRequiredAmount());
            pstmt.setBoolean(6, quest.isCompleted());
            if (quest.getDeadline() != null) {
                pstmt.setDate(7, java.sql.Date.valueOf(quest.getDeadline()));
            } else {
                pstmt.setNull(7, Types.DATE);
            }
            pstmt.setInt(8, quest.getId());
            pstmt.setInt(9, quest.getUserId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Deletes a quest from the database
     */
    public boolean deleteQuest(int questId, int userId) throws SQLException {
        String sql = "DELETE FROM quests WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questId);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Gets all quests for a specific user
     */
    public List<Quest> getQuestsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM quests WHERE user_id = ?";
        List<Quest> quests = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    quests.add(mapResultSetToQuest(rs));
                }
            }
        }
        
        return quests;
    }
    
    /**
     * Gets daily quests for a specific user
     */
    public List<Quest> getDailyQuestsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM quests WHERE user_id = ? AND quest_type = 'DAILY'";
        List<Quest> quests = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    quests.add(mapResultSetToQuest(rs));
                }
            }
        }
        
        return quests;
    }
    
    /**
     * Gets weekly quests for a specific user
     */
    public List<Quest> getWeeklyQuestsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM quests WHERE user_id = ? AND quest_type = 'WEEKLY'";
        List<Quest> quests = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    quests.add(mapResultSetToQuest(rs));
                }
            }
        }
        
        return quests;
    }
    
    /**
     * Gets monthly quests for a specific user
     */
    public List<Quest> getMonthlyQuestsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM quests WHERE user_id = ? AND quest_type = 'MONTHLY'";
        List<Quest> quests = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    quests.add(mapResultSetToQuest(rs));
                }
            }
        }
        
        return quests;
    }
    
    /**
     * Completes a quest and adds XP to the user
     */
    public boolean completeQuest(int questId, int userId) throws SQLException {
        String sql = "UPDATE quests SET completion_status = true WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questId);
            pstmt.setInt(2, userId);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                // Get the XP reward for this quest
                sql = "SELECT xp_reward FROM quests WHERE id = ?";
                try (PreparedStatement xpStmt = conn.prepareStatement(sql)) {
                    xpStmt.setInt(1, questId);
                    try (ResultSet rs = xpStmt.executeQuery()) {
                        if (rs.next()) {
                            int xpReward = rs.getInt("xp_reward");
                            // Add XP to user
                            return addUserXP(userId, xpReward);
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Adds XP to a user and updates their level if necessary
     */
    public boolean addUserXP(int userId, int xpAmount) throws SQLException {
        // First, get the current XP and level
        String sql = "SELECT current_xp, level FROM user_experience WHERE user_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            int currentXp = 0;
            int currentLevel = 1;
            boolean userExists = false;
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    currentXp = rs.getInt("current_xp");
                    currentLevel = rs.getInt("level");
                    userExists = true;
                }
            }
            
            // Calculate new XP and level
            int newXp = currentXp + xpAmount;
            int newLevel = calculateLevel(newXp);
            
            // Update or insert user experience
            if (userExists) {
                sql = "UPDATE user_experience SET current_xp = ?, level = ? WHERE user_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(sql)) {
                    updateStmt.setInt(1, newXp);
                    updateStmt.setInt(2, newLevel);
                    updateStmt.setInt(3, userId);
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                sql = "INSERT INTO user_experience (user_id, current_xp, level) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(sql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, newXp);
                    insertStmt.setInt(3, newLevel);
                    return insertStmt.executeUpdate() > 0;
                }
            }
        }
    }
    
    /**
     * Gets a user's current XP and level
     */
    public int[] getUserExperience(int userId) throws SQLException {
        String sql = "SELECT current_xp, level FROM user_experience WHERE user_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int currentXp = rs.getInt("current_xp");
                    int level = rs.getInt("level");
                    return new int[] { currentXp, level };
                }
            }
        }
        
        // If no record exists, create one with default values
        try (Connection conn = DatabaseManager.getConnection()) {
            String insertSql = "INSERT INTO user_experience (user_id, current_xp, level) VALUES (?, 0, 1)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.executeUpdate();
            }
        }
        
        return new int[] { 0, 1 }; // Default values
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
        Quest quest = new Quest();
        quest.setId(rs.getInt("id"));
        quest.setTitle(rs.getString("title"));
        quest.setDescription(rs.getString("description"));
        quest.setQuestType(rs.getString("quest_type"));
        quest.setXpReward(rs.getInt("xp_reward"));
        quest.setRequiredAmount(rs.getDouble("required_amount"));
        quest.setCompleted(rs.getBoolean("completion_status"));
        quest.setUserId(rs.getInt("user_id"));
        
        // Convert SQL Date to LocalDate if not null
        Date deadline = rs.getDate("deadline");
        if (deadline != null) {
            quest.setDeadline(deadline.toLocalDate());
        }
        
        return quest;
    }
    
    /**
     * Creates the necessary tables in the database if they don't exist
     */
    public void createQuestTablesIfNotExists() throws SQLException {
        // Create quests table
        String createQuestsTable = "CREATE TABLE IF NOT EXISTS quests (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL, " +
                "title VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "quest_type VARCHAR(50) NOT NULL, " +  // DAILY, WEEKLY, MONTHLY
                "xp_reward INTEGER NOT NULL, " +
                "required_amount DOUBLE PRECISION, " +
                "completion_status BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deadline DATE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        // Create user experience table
        String createUserExperienceTable = "CREATE TABLE IF NOT EXISTS user_experience (" +
                "user_id INTEGER PRIMARY KEY, " +
                "current_xp INTEGER NOT NULL DEFAULT 0, " +
                "level INTEGER NOT NULL DEFAULT 1, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createQuestsTable);
            stmt.execute(createUserExperienceTable);
        }
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
} 