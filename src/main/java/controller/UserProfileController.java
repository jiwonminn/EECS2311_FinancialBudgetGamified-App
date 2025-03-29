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

    public boolean updatePassword(String newPassword) throws SQLException {
        return userDao.updatePassword(userId, newPassword) > 0;
    }
}
