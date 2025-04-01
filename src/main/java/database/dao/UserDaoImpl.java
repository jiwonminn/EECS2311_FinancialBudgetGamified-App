package database.dao;

import model.User;
import database.DatabaseManager;
import java.sql.*;

public class UserDaoImpl implements UserDao {
    private final Connection connection;
    
    public UserDaoImpl() throws SQLException {
        this.connection = DatabaseManager.getConnection();
    }
    

    public UserDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User findUserById(int userId) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Create User object with all available fields
                    String email = rs.getString("email");
                    
                    // Get username from DB or use email prefix as fallback
                    String username = rs.getString("username");
                    if (username == null || username.isEmpty()) {
                        username = email.split("@")[0]; // Use part before @ as username
                    }
                    
                    String password = rs.getString("password");
                    double balance = 0.0; // Default balance
                    int points = 0; // Default points
                    return new User(userId, username, email, password, balance, points);
                }
            }
        }
        return null;
    }

    @Override
    public int authenticateUser(String email, String password) {
        String query = "SELECT id FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            System.out.println("Attempting to authenticate user with email: " + email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    System.out.println("Authentication successful. User ID: " + userId);
                    return userId;
                } else {
                    System.out.println("Authentication failed. No matching user found.");
                    return -1; // Indicates authentication failure
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception during authentication: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int registerUser(String email, String password) throws SQLException {
        int userId = authenticateUser(email, password);
        if (userId != -1) {
            System.out.println("User already exists with email: " + email);
            throw new SQLException("User with email " + email + " already exists");
        }

        String query = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            System.out.println("Attempting to register new user with email: " + email);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                    System.out.println("User registration successful. New user ID: " + userId);
                    return userId;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public int updatePassword(int userId, String newPassword) throws SQLException {
        String query = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate();
        }
    }

    @Override
    public String getPasswordForUser(int userId) throws SQLException {
        String query = "SELECT password FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        }
        return "";
    }

    @Override
    public boolean deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public int updateUsername(int userId, String newUsername) throws SQLException {
        // First check if the username is available
        if (!isUsernameAvailable(newUsername)) {
            throw new SQLException("Username '" + newUsername + "' is already taken");
        }
        
        String query = "UPDATE users SET username = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newUsername);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate();
        }
    }

    @Override
    public String getUsernameForUser(int userId) throws SQLException {
        String query = "SELECT username, email FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    // If username is not set, use email prefix
                    if (username == null || username.isEmpty()) {
                        String email = rs.getString("email");
                        return email.split("@")[0];
                    }
                    return username;
                }
            }
        }
        return "";
    }

    @Override
    public boolean isUsernameAvailable(String username) throws SQLException {
        String query = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                // If no record is found, the username is available
                return !rs.next();
            }
        }
    }
}