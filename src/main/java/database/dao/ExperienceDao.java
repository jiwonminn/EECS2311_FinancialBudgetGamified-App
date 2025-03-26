package database.dao;

import java.sql.SQLException;

public interface ExperienceDao {
    boolean addUserXP(int userId, int xpAmount) throws SQLException;
    int[] getUserExperience(int userId) throws SQLException;
    int getXpForNextLevel(int currentLevel);
}
