package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StubDatabaseConfig implements DatabaseConfig {
    private static StubDatabaseConfig instance;
    private final Map<Integer, Map<String, Object>> users = new HashMap<>();
    private int nextUserId = 1;

    public StubDatabaseConfig() {
        // Initialize with some default test data
        addTestUser("test@example.com", "password123");
        addTestUser("admin@example.com", "admin123");
    }

    public static StubDatabaseConfig getInstance() {
        if (instance == null) {
            instance = new StubDatabaseConfig();
        }
        return instance;
    }

    private void addTestUser(String email, String password) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("balance", 0.0);
        userData.put("points", 0);
        users.put(nextUserId++, userData);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new StubConnection(users);
    }

    @Override
    public void initializeDatabase() throws SQLException {
        // No-op for stub database
    }

    @Override
    public void closeConnection() throws SQLException {
        // No-op for stub database
    }
} 