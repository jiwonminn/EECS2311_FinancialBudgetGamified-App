package database.dao;

import model.User;
import java.sql.SQLException;

public interface UserDao {
    User findUserById(int userId) throws SQLException;
    int authenticateUser(String email, String password) throws SQLException;
    int registerUser(String email, String password) throws SQLException;
    int updatePassword(int userId, String newPassword) throws SQLException;
    // New: get the current password from the database
    String getPasswordForUser(int userId) throws SQLException;
}
