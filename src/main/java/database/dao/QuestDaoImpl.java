package database.dao;

import model.Quest;
import database.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class QuestDaoImpl implements QuestDao {
    private ExperienceDao experienceDao;
    @Override
    public int createQuest(Quest quest) throws SQLException {
        String sql = "INSERT INTO quests (title, description, quest_type, xp_reward, required_amount, completion_status, deadline, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, quest.getTitle());
            pstmt.setString(2, quest.getDescription());
            pstmt.setString(3, quest.getQuestType());
            pstmt.setInt(4, quest.getXpReward());
            pstmt.setDouble(5, quest.getRequiredAmount());
            pstmt.setBoolean(6, quest.isCompleted());
            if (quest.getDeadline() != null) {
                pstmt.setDate(7, java.sql.Date.valueOf(quest.getDeadline()));
            } else {
                pstmt.setNull(7, Types.DATE);
            }
            pstmt.setInt(8, quest.getUserId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public boolean updateQuest(Quest quest) throws SQLException {
        String sql = "UPDATE quests SET title = ?, description = ?, quest_type = ?, xp_reward = ?, required_amount = ?, completion_status = ?, deadline = ? " +
                "WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quest.getTitle());
            pstmt.setString(2, quest.getDescription());
            pstmt.setString(3, quest.getQuestType());
            pstmt.setInt(4, quest.getXpReward());
            pstmt.setDouble(5, quest.getRequiredAmount());
            pstmt.setBoolean(6, quest.isCompleted());
            if (quest.getDeadline() != null) {
                pstmt.setDate(7, java.sql.Date.valueOf(quest.getDeadline()));
            } else {
                pstmt.setNull(7, Types.DATE);
            }
            pstmt.setInt(8, quest.getId());
            pstmt.setInt(9, quest.getUserId());
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteQuest(int questId, int userId) throws SQLException {
        String sql = "DELETE FROM quests WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Quest> getQuestsByUserId(int userId) throws SQLException {
        List<Quest> quests = new ArrayList<>();
        String sql = "SELECT * FROM quests WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    quests.add(mapResultSetToQuest(rs));
                }
            }
        }
        return quests;
    }

    @Override
    public List<Quest> getActiveQuestsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM quests WHERE user_id = ? AND completion_status = false";
        List<Quest> quests = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quests.add(mapResultSetToQuest(rs));
                }
            }
        }

        return quests;
    }

    @Override
    public List<Quest> getDailyQuestsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM quests WHERE user_id = ? AND quest_type = 'DAILY'";
        List<Quest> quests = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    quests.add(mapResultSetToQuest(rs));
                }
            }
        }

        return quests;
    }

    @Override
    public List<Quest> getWeeklyQuestsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM quests WHERE user_id = ? AND quest_type = 'WEEKLY'";
        List<Quest> quests = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    quests.add(mapResultSetToQuest(rs));
                }
            }
        }

        return quests;    }

    @Override
    public List<Quest> getMonthlyQuestsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM quests WHERE user_id = ? AND quest_type = 'MONTHLY'";
        List<Quest> quests = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    quests.add(mapResultSetToQuest(rs));
                }
            }
        }

        return quests;
    }

    @Override
    public boolean completeQuest(int questId, int userId) throws SQLException {
        String sql = "UPDATE quests SET completion_status = true WHERE id = ? AND user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, questId);
            pstmt.setInt(2, userId);

            int result = pstmt.executeUpdate();

            if (result > 0) {
                // Get the XP reward for this quest
                sql = "SELECT xp_reward FROM quests WHERE id = ?";
                try (PreparedStatement xpStmt = conn.prepareStatement(sql)) {
                    xpStmt.setInt(1, questId);
                    try (ResultSet rs = xpStmt.executeQuery()) {
                        if (rs.next()) {
                            int xpReward = rs.getInt("xp_reward");
                            // Add XP to user
                            return experienceDao.addUserXP(userId, xpReward);
                        }
                    }
                }
            }
        }

        return false;
    }

    public Quest mapResultSetToQuest(ResultSet rs) throws SQLException {
        Quest quest = new Quest();
        quest.setId(rs.getInt("id"));
        quest.setTitle(rs.getString("title"));
        quest.setDescription(rs.getString("description"));
        quest.setQuestType(rs.getString("quest_type"));
        quest.setXpReward(rs.getInt("xp_reward"));
        quest.setRequiredAmount(rs.getDouble("required_amount"));
        quest.setCompleted(rs.getBoolean("completion_status"));
        quest.setUserId(rs.getInt("user_id"));
        Date deadline = rs.getDate("deadline");
        if (deadline != null) {
            quest.setDeadline(deadline.toLocalDate());
        }
        return quest;
    }


}

