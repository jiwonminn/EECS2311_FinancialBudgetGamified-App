package database.dao;

import database.DatabaseManager;
import java.sql.*;

public class ExperienceDaoImpl implements ExperienceDao {

    @Override
    public boolean addUserXP(int userId, int xpAmount) throws SQLException {
        String sql = "SELECT current_xp, level FROM user_experience WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int currentXp = 0;
            int currentLevel = 1;
            boolean userExists = false;
            try (ResultSet rs = pstmt.executeQuery()){
                if (rs.next()){
                    currentXp = rs.getInt("current_xp");
                    currentLevel = rs.getInt("level");
                    userExists = true;
                }
            }
            int newXp = currentXp + xpAmount;
            int newLevel = calculateLevel(newXp);
            if (userExists) {
                sql = "UPDATE user_experience SET current_xp = ?, level = ? WHERE user_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(sql)){
                    updateStmt.setInt(1, newXp);
                    updateStmt.setInt(2, newLevel);
                    updateStmt.setInt(3, userId);
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                sql = "INSERT INTO user_experience (user_id, current_xp, level) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(sql)){
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, newXp);
                    insertStmt.setInt(3, newLevel);
                    return insertStmt.executeUpdate() > 0;
                }
            }
        }
    }

    private int calculateLevel(int xp) {
        return (int) Math.floor(Math.sqrt(xp / 100.0));
    }

    @Override
    public int[] getUserExperience(int userId) throws SQLException {
        String sql = "SELECT current_xp, level FROM user_experience WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()){
                if (rs.next()){
                    int currentXp = rs.getInt("current_xp");
                    int level = rs.getInt("level");
                    return new int[]{currentXp, level};
                }
            }
        }
        // Initialize if no record exists.
        try (Connection conn = DatabaseManager.getConnection()){
            sql = "INSERT INTO user_experience (user_id, current_xp, level) VALUES (?, 0, 1)";
            try (PreparedStatement insertStmt = conn.prepareStatement(sql)){
                insertStmt.setInt(1, userId);
                insertStmt.executeUpdate();
            }
        }
        return new int[]{0, 1};
    }

    @Override
    public int getXpForNextLevel(int currentLevel) {
        return ((currentLevel + 1) * (currentLevel + 1)) * 100;
    }
}
