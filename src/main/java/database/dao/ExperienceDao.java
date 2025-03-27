package database.dao;

import java.sql.SQLException;

public interface ExperienceDao {
    boolean addUserXP(int userId, int xpAmount) throws SQLException;
    int[] getUserExperience(int userId) throws SQLException;
    int getXpForNextLevel(int currentLevel);
    
    /**
     * A null-safe method to add XP to a user that handles errors gracefully
     * @param userId The user ID
     * @param xpAmount The amount of XP to add
     * @return true if successful, false otherwise but never throws exception
     */
    default boolean addUserXPSafely(int userId, int xpAmount) {
        try {
            return addUserXP(userId, xpAmount);
        } catch (Exception e) {
            System.out.println("Error safely adding XP: " + e.getMessage());
            return false;
        }
    }
}
