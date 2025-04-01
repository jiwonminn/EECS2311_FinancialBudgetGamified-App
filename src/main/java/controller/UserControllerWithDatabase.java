package controller;

import database.dao.UserDao;
import database.dao.UserDaoImpl;
import model.User;
import java.sql.Connection;
import java.sql.SQLException;

public class UserControllerWithDatabase {
    private UserDao userDao;

    public UserControllerWithDatabase() throws SQLException {
        // Create an instance of the DAO implementation.
        this.userDao = new UserDaoImpl();
    }

    public UserControllerWithDatabase(Connection connection) {
        // Create an instance of the DAO implementation with the provided connection.
        this.userDao = new UserDaoImpl(connection);
    }

    public int authenticateUser(String email, String password) throws SQLException {
        return userDao.authenticateUser(email, password);
    }

    public int registerUser(String email, String password) throws SQLException {
        return userDao.registerUser(email, password);
    }
}