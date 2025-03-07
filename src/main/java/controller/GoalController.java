package controller;

import model.Goal;
import model.Transaction;
import database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller class for managing financial goals
 */
public class GoalController {
    
    /**
     * Creates a new goal in the database
     * @param goal The goal to create
     * @return The ID of the newly created goal
     */
    public int createGoal(Goal goal) throws SQLException {
        String sql = "INSERT INTO goals (user_id, title, description, target_amount, current_amount, " +
                "start_date, target_date, category, completed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, goal.getUserId());
            stmt.setString(2, goal.getTitle());
            stmt.setString(3, goal.getDescription());
            stmt.setDouble(4, goal.getTargetAmount());
            stmt.setDouble(5, goal.getCurrentAmount());
            stmt.setDate(6, new java.sql.Date(goal.getStartDate().getTime()));
            stmt.setDate(7, new java.sql.Date(goal.getTargetDate().getTime()));
            stmt.setString(8, goal.getCategory());
            stmt.setBoolean(9, goal.isCompleted());
            
            stmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    goal.setId(id);
                    return id;
                }
            }
        }
        
        return -1; // Failed to get ID
    }
    
    /**
     * Updates an existing goal in the database
     * @param goal The goal to update
     * @return True if successful, false otherwise
     */
    public boolean updateGoal(Goal goal) throws SQLException {
        String sql = "UPDATE goals SET title = ?, description = ?, target_amount = ?, " +
                "current_amount = ?, start_date = ?, target_date = ?, category = ?, " +
                "completed = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, goal.getTitle());
            stmt.setString(2, goal.getDescription());
            stmt.setDouble(3, goal.getTargetAmount());
            stmt.setDouble(4, goal.getCurrentAmount());
            stmt.setDate(5, new java.sql.Date(goal.getStartDate().getTime()));
            stmt.setDate(6, new java.sql.Date(goal.getTargetDate().getTime()));
            stmt.setString(7, goal.getCategory());
            stmt.setBoolean(8, goal.isCompleted());
            stmt.setInt(9, goal.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Deletes a goal from the database
     * @param goalId The ID of the goal to delete
     * @return True if successful, false otherwise
     */
    public boolean deleteGoal(int goalId) throws SQLException {
        String sql = "DELETE FROM goals WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, goalId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Gets a goal by its ID
     * @param goalId The ID of the goal to retrieve
     * @return The goal, or null if not found
     */
    public Goal getGoalById(int goalId) throws SQLException {
        String sql = "SELECT * FROM goals WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, goalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGoal(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Gets all goals for a user
     * @param userId The ID of the user
     * @return A list of goals
     */
    public List<Goal> getGoalsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM goals WHERE user_id = ? ORDER BY target_date";
        List<Goal> goals = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapResultSetToGoal(rs));
                }
            }
        }
        
        return goals;
    }
    
    /**
     * Gets all active (non-completed) goals for a user
     * @param userId The ID of the user
     * @return A list of active goals
     */
    public List<Goal> getActiveGoalsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM goals WHERE user_id = ? AND completed = false ORDER BY target_date";
        List<Goal> goals = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapResultSetToGoal(rs));
                }
            }
        }
        
        return goals;
    }
    
    /**
     * Update goal progress based on transactions
     * @param goalId The ID of the goal to update
     * @return True if successful, false otherwise
     */
    public boolean updateGoalProgress(int goalId) throws SQLException {
        // First get the goal to update
        Goal goal = getGoalById(goalId);
        if (goal == null) {
            return false;
        }
        
        // Get transactions in the goal's category
        TransactionController transactionController = new TransactionController();
        List<Transaction> transactions = transactionController.getTransactionsByCategoryAndDateRange(
                goal.getUserId(), goal.getCategory(), goal.getStartDate(), goal.getTargetDate());
        
        // Calculate total amount from savings transactions
        double totalSavings = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equalsIgnoreCase("income")) {
                totalSavings += transaction.getAmount();
            }
        }
        
        // Update the goal's current amount
        goal.setCurrentAmount(totalSavings);
        
        // Save the updated goal
        return updateGoal(goal);
    }
    
    /**
     * Helper method to map a ResultSet row to a Goal object
     */
    private Goal mapResultSetToGoal(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        double targetAmount = rs.getDouble("target_amount");
        double currentAmount = rs.getDouble("current_amount");
        Date startDate = rs.getDate("start_date");
        Date targetDate = rs.getDate("target_date");
        String category = rs.getString("category");
        boolean completed = rs.getBoolean("completed");
        
        return new Goal(id, userId, title, description, targetAmount, 
                        currentAmount, startDate, targetDate, category, completed);
    }
    
    /**
     * Creates the goals table in the database if it doesn't exist
     */
    public void createGoalsTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS goals (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL, " +
                "title VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "target_amount DOUBLE PRECISION NOT NULL, " +
                "current_amount DOUBLE PRECISION NOT NULL DEFAULT 0, " +
                "start_date DATE NOT NULL, " +
                "target_date DATE NOT NULL, " +
                "category VARCHAR(100) NOT NULL, " +
                "completed BOOLEAN NOT NULL DEFAULT FALSE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
} 