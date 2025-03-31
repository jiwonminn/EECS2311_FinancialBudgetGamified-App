package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StubDatabaseConfig implements DatabaseConfig {
    private static StubDatabaseConfig instance;
    private final Map<Integer, Map<String, Object>> users = new HashMap<>();
    private final Map<Integer, Map<String, Object>> userExperience = new HashMap<>();
    private int nextUserId = 1;
    private boolean isTestMode = false;

    public StubDatabaseConfig() {
        // Only initialize with test data if not in test mode
        if (!isTestMode) {
            addTestUser("test@example.com", "password123");
            addTestUser("admin@example.com", "admin123");
        }
    }

    public StubDatabaseConfig(boolean isTestMode) {
        this.isTestMode = isTestMode;
        if (!isTestMode) {
            addTestUser("test@example.com", "password123");
            addTestUser("admin@example.com", "admin123");
        }
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
        users.put(nextUserId, userData);

        // Initialize user experience
        Map<String, Object> experienceData = new HashMap<>();
        experienceData.put("current_xp", 0);
        experienceData.put("level", 1);
        userExperience.put(nextUserId, experienceData);

        nextUserId++;
    }

    public Map<Integer, Map<String, Object>> getUsers() {
        return users;
    }

    public Map<Integer, Map<String, Object>> getUserExperience() {
        return userExperience;
    }

    public int getNextUserId() {
        return nextUserId;
    }

    public void incrementNextUserId() {
        nextUserId++;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new StubConnection(users, userExperience);
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