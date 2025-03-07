package database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.UserGoal;

/**
 * Data Access Object for UserGoal
 */
public class UserGoalDAO {
    
    /**
     * Add a new goal for a user
     * @param goal the goal to add
     * @return the ID of the inserted goal, or -1 if the operation failed
     */
    public int addGoal(UserGoal goal) {
        String sql = "INSERT INTO user_goals (user_id, goal_type, goal_name, target_amount, " +
                     "current_amount, start_date, end_date, is_completed, reminder_frequency) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, goal.getUserId());
            pstmt.setString(2, goal.getGoalType().name());
            pstmt.setString(3, goal.getGoalName());
            pstmt.setDouble(4, goal.getTargetAmount());
            pstmt.setDouble(5, goal.getCurrentAmount());
            pstmt.setDate(6, Date.valueOf(goal.getStartDate()));
            pstmt.setDate(7, Date.valueOf(goal.getEndDate()));
            pstmt.setBoolean(8, goal.isCompleted());
            pstmt.setString(9, goal.getReminderFrequency().name());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding goal: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Update an existing goal
     * @param goal the goal to update
     * @return true if the update was successful
     */
    public boolean updateGoal(UserGoal goal) {
        String sql = "UPDATE user_goals SET goal_type = ?, goal_name = ?, target_amount = ?, " +
                     "current_amount = ?, start_date = ?, end_date = ?, is_completed = ?, " +
                     "reminder_frequency = ?, last_reminder_date = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, goal.getGoalType().name());
            pstmt.setString(2, goal.getGoalName());
            pstmt.setDouble(3, goal.getTargetAmount());
            pstmt.setDouble(4, goal.getCurrentAmount());
            pstmt.setDate(5, Date.valueOf(goal.getStartDate()));
            pstmt.setDate(6, Date.valueOf(goal.getEndDate()));
            pstmt.setBoolean(7, goal.isCompleted());
            pstmt.setString(8, goal.getReminderFrequency().name());
            pstmt.setDate(9, goal.getLastReminderDate() != null ? 
                    Date.valueOf(goal.getLastReminderDate()) : null);
            pstmt.setInt(10, goal.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating goal: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete a goal
     * @param goalId the ID of the goal to delete
     * @return true if the deletion was successful
     */
    public boolean deleteGoal(int goalId) {
        String sql = "DELETE FROM user_goals WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, goalId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting goal: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get a goal by ID
     * @param goalId the ID of the goal
     * @return the goal if found, null otherwise
     */
    public UserGoal getGoalById(int goalId) {
        String sql = "SELECT * FROM user_goals WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, goalId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToGoal(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting goal: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all goals for a user
     * @param userId the user ID
     * @return a list of goals
     */
    public List<UserGoal> getGoalsByUserId(int userId) {
        List<UserGoal> goals = new ArrayList<>();
        String sql = "SELECT * FROM user_goals WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                goals.add(mapResultSetToGoal(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting goals: " + e.getMessage());
            e.printStackTrace();
        }
        
        return goals;
    }
    
    /**
     * Get all incomplete goals for a user
     * @param userId the user ID
     * @return a list of incomplete goals
     */
    public List<UserGoal> getIncompleteGoalsByUserId(int userId) {
        List<UserGoal> goals = new ArrayList<>();
        String sql = "SELECT * FROM user_goals WHERE user_id = ? AND is_completed = false ORDER BY end_date ASC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                goals.add(mapResultSetToGoal(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting incomplete goals: " + e.getMessage());
            e.printStackTrace();
        }
        
        return goals;
    }
    
    /**
     * Update the last reminder date for a goal
     * @param goalId the goal ID
     * @param reminderDate the date the reminder was sent
     * @return true if the update was successful
     */
    public boolean updateLastReminderDate(int goalId, LocalDate reminderDate) {
        String sql = "UPDATE user_goals SET last_reminder_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(reminderDate));
            pstmt.setInt(2, goalId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating reminder date: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Maps a ResultSet row to a UserGoal object
     * @param rs the ResultSet
     * @return the UserGoal object
     * @throws SQLException if a database error occurs
     */
    private UserGoal mapResultSetToGoal(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        UserGoal.GoalType goalType = UserGoal.GoalType.valueOf(rs.getString("goal_type"));
        String goalName = rs.getString("goal_name");
        double targetAmount = rs.getDouble("target_amount");
        double currentAmount = rs.getDouble("current_amount");
        LocalDate startDate = rs.getDate("start_date").toLocalDate();
        LocalDate endDate = rs.getDate("end_date").toLocalDate();
        boolean isCompleted = rs.getBoolean("is_completed");
        UserGoal.ReminderFrequency reminderFrequency = 
                UserGoal.ReminderFrequency.valueOf(rs.getString("reminder_frequency"));
        
        LocalDate lastReminderDate = null;
        if (rs.getDate("last_reminder_date") != null) {
            lastReminderDate = rs.getDate("last_reminder_date").toLocalDate();
        }
        
        // Convert timestamp to LocalDate properly
        LocalDate createdAt = null;
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            createdAt = createdAtTimestamp.toLocalDateTime().toLocalDate();
        } else {
            createdAt = LocalDate.now(); // Default to today if null
        }
        
        return new UserGoal(id, userId, goalType, goalName, targetAmount, currentAmount, 
                startDate, endDate, isCompleted, reminderFrequency, lastReminderDate, createdAt);
    }
} 