package controller;

import database.dao.LeaderboardDao;
import database.dao.LeaderboardDaoImpl;
import model.LeaderboardEntry;

import java.sql.SQLException;
import java.util.List;

public class LeaderboardController {
    private int userId;

    public LeaderboardController(int userId) {
        this.userId = userId;
    }

    // Retrieve the leaderboard entries, sorted by level (and XP)
    public List<LeaderboardEntry> getLeaderboard() throws SQLException {
        LeaderboardDao leaderboardDao = new LeaderboardDaoImpl();
        return leaderboardDao.fetchLeaderboard();
    }
}
