package database.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import model.Quest;

public interface QuestDao {
    int createQuest(Quest quest) throws SQLException;
    boolean updateQuest(Quest quest) throws SQLException;
    boolean deleteQuest(int questId, int userId) throws SQLException;
    List<Quest> getQuestsByUserId(int userId) throws SQLException;
    List<Quest> getActiveQuestsByUserId(int userId) throws SQLException;
    List<Quest> getDailyQuestsByUserId(int userId) throws SQLException;
    List<Quest> getWeeklyQuestsByUserId(int userId) throws SQLException;
    List<Quest> getMonthlyQuestsByUserId(int userId) throws SQLException;
    boolean completeQuest(int questId, int userId) throws SQLException;
    Quest mapResultSetToQuest(ResultSet rs) throws SQLException;
    
    /**
     * Get a quest by its ID
     * @param questId The quest ID
     * @return The Quest object, or null if not found
     * @throws SQLException if there's a database error
     */
    Quest getQuestById(int questId) throws SQLException;
}

