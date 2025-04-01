package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StubDatabaseConfig implements DatabaseConfig {
    private static StubDatabaseConfig instance;
    private final Map<Integer, Map<String, Object>> users = new HashMap<>();
    private final Map<Integer, Map<String, Object>> userExperience = new HashMap<>();
    private final Map<Integer, Map<String, Object>> transactions = new HashMap<>();
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

    public static void resetInstance() {
        if (instance != null) {
            instance.clearTransactions();
            instance.clearUsers();
            instance.clearUserExperience();
        }
        instance = null;
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

    public Map<Integer, Map<String, Object>> getTransactions() {
        return transactions;
    }

    public int getNextUserId() {
        return nextUserId;
    }

    public void incrementNextUserId() {
        nextUserId++;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new StubConnection(users, userExperience, transactions);
    }

    @Override
    public void initializeDatabase() throws SQLException {
        // No-op for stub database
    }

    @Override
    public void closeConnection() throws SQLException {
        // No-op for stub database
    }

    public void clearTransactions() {
        System.out.println("Clearing all transactions from StubDatabaseConfig");
        transactions.clear();
    }

    public boolean isTransactionsEmpty() {
        boolean isEmpty = transactions.isEmpty();
        System.out.println("Checking if transactions are empty: " + isEmpty + " (size=" + transactions.size() + ")");
        return isEmpty;
    }

    public void clearUsers() {
        System.out.println("Clearing all users from StubDatabaseConfig");
        users.clear();
        nextUserId = 1;
    }

    public void clearUserExperience() {
        System.out.println("Clearing all user experience data from StubDatabaseConfig");
        userExperience.clear();
    }

    public boolean isUsersEmpty() {
        boolean isEmpty = users.isEmpty();
        System.out.println("Checking if users are empty: " + isEmpty + " (size=" + users.size() + ")");
        return isEmpty;
    }
} 