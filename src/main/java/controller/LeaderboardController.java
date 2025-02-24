package controller;

import model.Leaderboard;
import database.DatabaseManager;
import java.sql.*;
import java.util.List;

public class LeaderboardController {
    private Leaderboard leaderboard;
    private Connection connection;
    
    public LeaderboardController() {
        this.leaderboard = new Leaderboard();
        this.connection = DatabaseManager.getConnection();
        loadLeaderboardFromDatabase();
    }
    
    public void addScore(String username, int points) {
        leaderboard.addEntry(username, points);
        saveToDatabase(username, points);
    }
    
    private void loadLeaderboardFromDatabase() {
        String query = "SELECT username, points FROM leaderboard ORDER BY points DESC LIMIT 10";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                leaderboard.addEntry(
                    rs.getString("username"),
                    rs.getInt("points")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void saveToDatabase(String username, int points) {
        String query = "INSERT INTO leaderboard (username, points) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setInt(2, points);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Leaderboard.LeaderboardEntry> getTopScores() {
        return leaderboard.getTopEntries();
    }
}
