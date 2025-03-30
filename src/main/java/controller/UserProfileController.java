package controller;

import database.dao.UserDao;
import database.dao.UserDaoImpl;
import model.User;
import java.sql.SQLException;

public class UserProfileController {
    private int userId;
    private UserDao userDao;

    public UserProfileController(int userId) throws SQLException {
        this.userId = userId;
        this.userDao = new UserDaoImpl();
    }

    public User getUserProfile() throws SQLException {
        return userDao.findUserById(userId);
    }

    // Retrieve password directly from the DB since User model doesn't store it
    public String getUserPassword() throws SQLException {
        return userDao.getPasswordForUser(userId);
    }

    // Get current username from the database
    public String getUsername() throws SQLException {
        return userDao.getUsernameForUser(userId);
    }

    // Check if a username is available (not already taken)
    public boolean isUsernameAvailable(String username) throws SQLException {
        return userDao.isUsernameAvailable(username);
    }

    public boolean updatePassword(String newPassword) throws SQLException {
        return userDao.updatePassword(userId, newPassword) > 0;
    }

    // Update username in the database
    public boolean updateUsername(String newUsername) throws SQLException {
        return userDao.updateUsername(userId, newUsername) > 0;
    }
}
