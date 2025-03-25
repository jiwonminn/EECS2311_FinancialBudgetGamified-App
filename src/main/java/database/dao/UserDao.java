package database.dao;

import model.User;
import java.sql.SQLException;

public interface UserDao {
    User findUserById(int userId) throws SQLException;
    User authenticateUser(String email, String password) throws SQLException;
    int registerUser(String email, String password) throws SQLException;
}
