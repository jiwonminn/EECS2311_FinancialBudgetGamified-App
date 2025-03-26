package database.dao;

import model.Goal;
import model.Quest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface GoalDao {
    int createGoal(Goal goal) throws SQLException;
    boolean updateGoal(Goal goal) throws SQLException;
    boolean deleteGoal(int goalId) throws SQLException;
    Goal getGoalById(int goalId) throws SQLException;
    List<Goal> getGoalsByUserId(int userId) throws SQLException;
    List<Goal> getActiveGoalsByUserId(int userId) throws SQLException;
}
