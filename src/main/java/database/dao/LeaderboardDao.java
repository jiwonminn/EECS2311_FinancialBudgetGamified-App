package database.dao;

import model.LeaderboardEntry;
import java.sql.SQLException;
import java.util.List;

public interface LeaderboardDao {
    List<LeaderboardEntry> fetchLeaderboard() throws SQLException;
}
