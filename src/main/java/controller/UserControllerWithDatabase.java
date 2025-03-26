package controller;

import database.dao.UserDao;
import database.dao.UserDaoImpl;
import model.User;
import java.sql.SQLException;

public class UserControllerWithDatabase {
    private UserDao userDao;

    public UserControllerWithDatabase() {
        // Create an instance of the DAO implementation.
        this.userDao = new UserDaoImpl();
    }

    public int authenticateUser(String email, String password) throws SQLException {
        return userDao.authenticateUser(email, password);
    }

    public int registerUser(String email, String password) throws SQLException {
        return userDao.registerUser(email, password);
    }
}
