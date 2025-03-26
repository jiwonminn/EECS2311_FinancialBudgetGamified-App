package database.dao;

import model.User;
import database.DatabaseManager;
import java.sql.*;

public class UserDaoImpl implements UserDao {

    @Override
    public User findUserById(int userId) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Assuming User has an appropriate constructor
                    return new User(rs.getString("username"), rs.getString("email"), rs.getDouble("balance"));
                }
            }
        }
        return null;
    }

    @Override
    public  int authenticateUser(String email, String password) {
        String query = "SELECT id FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1; // Indicates authentication failure
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    @Override
    public  int registerUser(String email, String password) {

        int userId = authenticateUser(email, password);
        if (userId != -1) {
            return -1; // Indicates user already exists
        }

        String query = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Returns the generated id
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Indicates failure
        }
    }
}
