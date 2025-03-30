package database.dao;

import model.LeaderboardEntry;
import database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardDaoImpl implements LeaderboardDao {
    @Override
    public List<LeaderboardEntry> fetchLeaderboard() throws SQLException {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        // Join the users and user_experience tables to get username, level, and XP
        String query = "SELECT u.id, u.email, u.username, ue.level, ue.current_xp as xp " +
                "FROM users u " +
                "JOIN user_experience ue ON u.id = ue.user_id " +
                "ORDER BY ue.level DESC, ue.current_xp DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            int rank = 1;
            while (rs.next()) {
                // Try to get username first
                String username = rs.getString("username");
                
                // If username is null or empty, use email prefix as fallback
                if (username == null || username.isEmpty()) {
                    String userEmail = rs.getString("email");
                    // Extract portion before "@" if present
                    if (userEmail != null && userEmail.contains("@")) {
                        username = userEmail.substring(0, userEmail.indexOf("@"));
                    } else {
                        username = userEmail;
                    }
                }
                
                int level = rs.getInt("level");
                int xp = rs.getInt("xp");
                leaderboard.add(new LeaderboardEntry(rank, username, level, xp));
                rank++;
            }
        }
        return leaderboard;
    }
}
