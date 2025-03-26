package database.dao;


import model.Quest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
}

