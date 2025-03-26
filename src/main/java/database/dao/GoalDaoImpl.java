package database.dao;

import model.Goal;
import database.DatabaseManager;
import model.Quest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class GoalDaoImpl implements GoalDao {

    @Override
    public int createGoal(Goal goal) throws SQLException {
        String sql = "INSERT INTO goals (user_id, title, description, target_amount, current_amount, start_date, target_date, category, completed) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    goal.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean updateGoal(Goal goal) throws SQLException {
        String sql = "UPDATE goals SET title = ?, description = ?, target_amount = ?, current_amount = ?, start_date = ?, target_date = ?, category = ?, completed = ? WHERE id = ?";
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
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteGoal(int goalId) throws SQLException {
        String sql = "DELETE FROM goals WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, goalId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
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

    @Override
    public List<Goal> getGoalsByUserId(int userId) throws SQLException {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT * FROM goals WHERE user_id = ? ORDER BY target_date";
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

    @Override
    public List<Goal> getActiveGoalsByUserId(int userId) throws SQLException {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT * FROM goals WHERE user_id = ? AND completed = false ORDER BY target_date";
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
        return new Goal(id, userId, title, description, targetAmount, currentAmount, startDate, targetDate, category, completed);
    }
}

