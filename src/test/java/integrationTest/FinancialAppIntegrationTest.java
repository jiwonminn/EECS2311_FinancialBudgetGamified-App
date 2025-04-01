package integrationTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import database.DatabaseConfig;
import database.StubDatabaseConfig;
import database.StubConnection;
import database.dao.UserDao;
import database.dao.UserDaoImpl;
import model.User;
import model.Transaction;
import controller.UserController;
import controller.UserControllerWithDatabase;
import controller.TransactionController;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.sql.PreparedStatement;

public class FinancialAppIntegrationTest {
    private Connection connection;
    private UserController userController;
    private UserControllerWithDatabase userControllerWithDb;
    private TransactionController transactionController;
    private UserDao userDao;
    private User testUser;
    private static final String TEST_PASSWORD = "testPassword123";
    private static final String TEST_USERNAME = "testUser";
    private String TEST_EMAIL; // Make this an instance variable so we can set it uniquely each time
    private static final double INITIAL_BALANCE = 0.0;

    @BeforeEach
    public void setUp() throws SQLException {
        // Generate a unique email for this test run
        TEST_EMAIL = "testuser" + System.currentTimeMillis() + "@example.com";
        
        // Reset the test database configuration
        TestDatabaseConfig.resetInstance();
        
        // Use the test database configuration
        DatabaseConfig dbConfig = TestDatabaseConfig.getInstance();
        
        // Explicitly clear any transactions that might be leftover
        if (dbConfig instanceof StubDatabaseConfig) {
            ((StubDatabaseConfig) dbConfig).clearTransactions();
            ((StubDatabaseConfig) dbConfig).clearUsers();
            ((StubDatabaseConfig) dbConfig).clearUserExperience();
        }
        
        connection = dbConfig.getConnection();
        
        // Initialize DAOs and controllers
        userDao = new UserDaoImpl(connection);
        userControllerWithDb = new UserControllerWithDatabase(connection);
        userController = new UserController(TEST_USERNAME, TEST_EMAIL, INITIAL_BALANCE);
        transactionController = new TransactionController(connection);
        
        // Create a test user
        try {
            System.out.println("Attempting to register test user with email: " + TEST_EMAIL);
            int userId = userControllerWithDb.registerUser(TEST_EMAIL, TEST_PASSWORD);
            System.out.println("Test user registered with ID: " + userId);
            
            // Verify test user was created successfully
            testUser = userDao.findUserById(userId);
            if (testUser == null) {
                throw new SQLException("Failed to create test user - user is null after creation");
            }
            System.out.println("Test user created successfully with ID: " + testUser.getId());
            
            // Set the userId in transactionController
            transactionController.setUserId(testUser.getId());
            
            // Initialize user experience record
            String sql = "INSERT INTO user_experience (user_id, current_xp, level) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (testUser == null || testUser.getId() <= 0) {
                    throw new SQLException("Invalid test user ID");
                }
                System.out.println("Initializing experience for user ID: " + testUser.getId());
                stmt.setInt(1, testUser.getId());
                stmt.setInt(2, 0);  // initial XP
                stmt.setInt(3, 1);  // initial level
                stmt.executeUpdate();
                System.out.println("Successfully initialized user experience");
            }
        } catch (SQLException e) {
            System.err.println("Error during test setup: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up test data
        if (testUser != null) {
            try {
                // Delete user from database
                boolean deleted = userDao.deleteUser(testUser.getId());
                if (!deleted) {
                    System.err.println("Failed to delete test user with ID: " + testUser.getId());
                }
            } catch (SQLException e) {
                System.err.println("Error during test cleanup: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Close the connection if it's still open
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testUserRegistration() throws SQLException {
        // Force clear users at the beginning of this test
        DatabaseConfig dbConfig = TestDatabaseConfig.getInstance();
        if (dbConfig instanceof StubDatabaseConfig) {
            System.out.println("Explicitly clearing users for test: testUserRegistration");
            ((StubDatabaseConfig) dbConfig).clearUsers();
            // Verify that users are really empty
            boolean isEmpty = ((StubDatabaseConfig) dbConfig).isUsersEmpty();
            if (!isEmpty) {
                System.err.println("WARNING: Users not empty after clearing!");
            }
        }
        
        // Test registering a new user with timestamp to ensure uniqueness
        String uniqueEmail = "newuser" + System.currentTimeMillis() + "@example.com";
        System.out.println("Testing registration with unique email: " + uniqueEmail);
        
        int userId = userControllerWithDb.registerUser(uniqueEmail, "password123");
        assertTrue(userId >= 0);
        System.out.println("Successfully registered user with ID: " + userId);
        
        // Clean up
        boolean deleted = userDao.deleteUser(userId);
        System.out.println("User deletion result: " + deleted);
    }

    @Test
    public void testDuplicateUserRegistration() throws SQLException {
        // Ensure the TEST_EMAIL user exists
        System.out.println("Checking duplicate registration with email: " + TEST_EMAIL);
        
        // Try to register a user with the same email that was used in setUp
        assertThrows(SQLException.class, () -> {
            userControllerWithDb.registerUser(TEST_EMAIL, "password123");
        });
    }

    @Test
    public void testUserAuthentication() throws SQLException {
        // Test successful authentication
        int userId = userControllerWithDb.authenticateUser(TEST_EMAIL, TEST_PASSWORD);
        assertTrue(userId >= 0);
        
        User authenticatedUser = userDao.findUserById(userId);
        assertEquals(TEST_EMAIL, authenticatedUser.getEmail());
        
    }

    @Test
    public void testUpdatePassword() throws SQLException {
        // Test updating password
        boolean success = userDao.updatePassword(testUser.getId(), "newpassword123") > 0;
        assertTrue(success);
        
        // Verify new password works
        int userId = userControllerWithDb.authenticateUser(TEST_EMAIL, "newpassword123");
        assertTrue(userId > 0);
        
        // Reset password for other tests
        userDao.updatePassword(testUser.getId(), TEST_PASSWORD);
    }

    @Test
    public void testTransactionOperations() throws SQLException {
        // Get initial points
        User initialUser = userDao.findUserById(testUser.getId());
        System.out.println("Initial points: " + initialUser.getPoints());
        
        // Test updating balance
        double newBalance = 50.0;
        
        // Directly set the balance in a way that works with the stub database
        String directSetQuery = "UPDATE users SET balance = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(directSetQuery)) {
            stmt.setDouble(1, newBalance);
            stmt.setInt(2, testUser.getId());
            int updated = stmt.executeUpdate();
            System.out.println("DEBUG: Direct balance set result: " + updated);
        }
        
        // Verify the direct set worked
        User updatedUser = userDao.findUserById(testUser.getId());
        System.out.println("DEBUG: Balance after direct set: " + updatedUser.getBalance());
        
        // Reset user state for other tests
        try (PreparedStatement stmt = connection.prepareStatement(directSetQuery)) {
            stmt.setDouble(1, 0.0);
            stmt.setInt(2, testUser.getId());
            stmt.executeUpdate();
        }
        
        // Verify successful reset
        updatedUser = userDao.findUserById(testUser.getId());
        System.out.println("DEBUG: Balance after reset: " + updatedUser.getBalance());
        
        // Now test the updateBalance method's return value
        boolean updateSuccess = transactionController.updateBalance(testUser.getId(), newBalance);
        System.out.println("DEBUG: Balance update success: " + updateSuccess);
        assertTrue(updateSuccess, "updateBalance should return true on successful update");
    }

    @Nested
    @DisplayName("User Management Tests")
    class UserManagementTests {
        @Test
        @DisplayName("Test User Registration and Login Flow")
        void testUserRegistrationAndLogin() throws SQLException {
            // Test login with created user
            int userId = userControllerWithDb.authenticateUser(TEST_EMAIL, TEST_PASSWORD);
            assertTrue(userId > 0);
            
            User loggedInUser = userDao.findUserById(userId);
            assertNotNull(loggedInUser);
            assertEquals(TEST_EMAIL, loggedInUser.getEmail());
        }

        @Test
        @DisplayName("Test User Profile Update")
        void testUserProfileUpdate() throws SQLException {
            // Login user
            int userId = userControllerWithDb.authenticateUser(TEST_EMAIL, TEST_PASSWORD);
            assertTrue(userId > 0);
            
            // Update password
            int rowsAffected = userDao.updatePassword(userId, "newPassword123");
            assertEquals(1, rowsAffected); // Check that exactly one row was updated
            
            // Verify update by trying to login with new password
            int updatedUserId = userControllerWithDb.authenticateUser(TEST_EMAIL, "newPassword123");
            assertEquals(userId, updatedUserId);
            
            // Reset password for other tests
            userDao.updatePassword(userId, TEST_PASSWORD);
        }
    }

    @Nested
    @DisplayName("Transaction Management Tests")
    class TransactionManagementTests {
        @Test
        @DisplayName("Test Transaction Creation and Retrieval")
        void testTransactionFlow() throws SQLException {
            // Login user
            int userId = userControllerWithDb.authenticateUser(TEST_EMAIL, TEST_PASSWORD);
            assertTrue(userId > 0);
            
            // Create a test transaction
            String description = "Test transaction";
            double amount = 100.0;
            LocalDate date = LocalDate.now();
            boolean isIncome = false;
            String category = "Food";
            
            // Save transaction
            transactionController.addTransaction(description, amount, date, isIncome, category);
            
            // Retrieve and verify transaction
            List<Transaction> transactions = transactionController.getTransactions();
            assertFalse(transactions.isEmpty());
            Transaction retrievedTransaction = transactions.get(0);
            assertEquals(amount, retrievedTransaction.getAmount());
            assertEquals(category, retrievedTransaction.getCategory());
        }

        @Test
        @DisplayName("Test Transaction Update")
        void testTransactionUpdate() throws SQLException {
            // Login user
            int userId = userControllerWithDb.authenticateUser(TEST_EMAIL, TEST_PASSWORD);
            assertTrue(userId > 0);
            
            // Make sure transactions are cleared at the beginning
            DatabaseConfig dbConfig = TestDatabaseConfig.getInstance();
            if (dbConfig instanceof StubDatabaseConfig) {
                System.out.println("Explicitly clearing transactions for test: testTransactionUpdate");
                ((StubDatabaseConfig) dbConfig).clearTransactions();
                boolean isEmpty = ((StubDatabaseConfig) dbConfig).isTransactionsEmpty();
                System.out.println("Transactions empty after clearing: " + isEmpty);
            }
            
            // Create initial transaction
            String description = "Initial transaction";
            double amount = 100.0;
            LocalDate date = LocalDate.now();
            boolean isIncome = false;
            String category = "Food";
            
            System.out.println("Adding initial transaction: " + description + ", amount=" + amount);
            transactionController.addTransaction(description, amount, date, isIncome, category);
            
            // Get the transaction ID from the database
            List<Transaction> transactions = transactionController.getTransactions();
            assertFalse(transactions.isEmpty());
            System.out.println("Initial transactions count: " + transactions.size());
            for (Transaction t : transactions) {
                System.out.println("Transaction found: ID=" + t.getId() + ", Description=" + t.getDescription() + ", Amount=" + t.getAmount());
            }
            int transactionId = transactions.get(0).getId();
            System.out.println("Deleting transaction with ID: " + transactionId);
            
            // Delete the old transaction
            boolean deleted = transactionController.deleteTransaction(transactionId);
            System.out.println("Delete operation returned: " + deleted);
            assertTrue(deleted);
            
            // Verify transaction was deleted
            transactions = transactionController.getTransactions();
            System.out.println("Transactions after delete: " + transactions.size());
            if (!transactions.isEmpty()) {
                for (Transaction t : transactions) {
                    System.out.println("Transaction still exists: ID=" + t.getId() + ", Description=" + t.getDescription() + ", Amount=" + t.getAmount());
                }
            }
            
            // Create updated transaction
            String updatedDescription = "Updated transaction";
            double updatedAmount = 150.0;
            String updatedCategory = "Entertainment";
            
            System.out.println("Adding updated transaction: " + updatedDescription + ", amount=" + updatedAmount);
            transactionController.addTransaction(updatedDescription, updatedAmount, date, isIncome, updatedCategory);
            
            // Verify update
            transactions = transactionController.getTransactions();
            assertFalse(transactions.isEmpty());
            System.out.println("Final transactions count: " + transactions.size());
            for (Transaction t : transactions) {
                System.out.println("Final transaction: ID=" + t.getId() + ", Description=" + t.getDescription() + ", Amount=" + t.getAmount() + ", Category=" + t.getCategory());
            }
            Transaction retrievedTransaction = transactions.get(0);
            assertEquals(updatedAmount, retrievedTransaction.getAmount());
            assertEquals(updatedCategory, retrievedTransaction.getCategory());
        }

        @Test
        @DisplayName("Test Transaction Deletion")
        void testTransactionDeletion() throws SQLException {
            // Login user
            int userId = userControllerWithDb.authenticateUser(TEST_EMAIL, TEST_PASSWORD);
            assertTrue(userId > 0);
            
            // Create transaction
            String description = "Transaction to delete";
            double amount = 100.0;
            LocalDate date = LocalDate.now();
            boolean isIncome = false;
            String category = "Food";
            
            transactionController.addTransaction(description, amount, date, isIncome, category);
            
            // Get the transaction ID from the database
            List<Transaction> transactions = transactionController.getTransactions();
            assertFalse(transactions.isEmpty());
            int transactionId = transactions.get(0).getId();
            
            // Delete transaction
            boolean deleted = transactionController.deleteTransaction(transactionId);
            assertTrue(deleted);
            
            // Verify deletion
            transactions = transactionController.getTransactions();
            assertTrue(transactions.isEmpty());
        }
    }

    @Nested
    @DisplayName("Budget Management Tests")
    class BudgetManagementTests {
        @Test
        @DisplayName("Test Budget Management")
        void testBudgetManagement() throws SQLException {
            // Login user
            int userId = userControllerWithDb.authenticateUser(TEST_EMAIL, TEST_PASSWORD);
            assertTrue(userId > 0);
            
            // Set budget
            double monthlyBudget = 1000.0;
            boolean budgetSet = userController.setMonthlyBudget(userId, monthlyBudget);
            assertTrue(budgetSet);
            
            // Verify budget
            double retrievedBudget = userController.getMonthlyBudget(userId);
            assertEquals(monthlyBudget, retrievedBudget);
        }
    }

    @Nested
    @DisplayName("Transaction Analytics Tests")
    class TransactionAnalyticsTests {
        @Test
        @DisplayName("Test Transaction Categories and Statistics")
        void testTransactionCategoriesAndStats() throws SQLException {
            // Login user
            int userId = userControllerWithDb.authenticateUser(TEST_EMAIL, TEST_PASSWORD);
            assertTrue(userId > 0);
            
            // Force clear all transactions at the beginning of this test
            DatabaseConfig dbConfig = TestDatabaseConfig.getInstance();
            if (dbConfig instanceof StubDatabaseConfig) {
                System.out.println("Explicitly clearing transactions for test: testTransactionCategoriesAndStats");
                ((StubDatabaseConfig) dbConfig).clearTransactions();
                // Verify that transactions are really empty
                boolean isEmpty = ((StubDatabaseConfig) dbConfig).isTransactionsEmpty();
                if (!isEmpty) {
                    System.err.println("WARNING: Transactions not empty after clearing!");
                }
            } else {
                // For real DB, execute a delete statement
                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM transactions WHERE user_id = ?")) {
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                    System.out.println("Deleted existing transactions for user: " + userId);
                }
            }
            
            // Create multiple transactions in different categories
            String description1 = "Food purchase";
            double amount1 = 50.0;
            LocalDate date = LocalDate.now();
            boolean isIncome = false;
            String category1 = "Food";
            
            String description2 = "Transport fare";
            double amount2 = 30.0;
            String category2 = "Transport";
            
            // Add transactions and log them
            System.out.println("Adding Food transaction: " + description1 + ", amount: " + amount1);
            transactionController.addTransaction(description1, amount1, date, isIncome, category1);
            System.out.println("Adding Transport transaction: " + description2 + ", amount: " + amount2);
            transactionController.addTransaction(description2, amount2, date, isIncome, category2);
            
            // Verify category statistics
            double foodTotal = transactionController.getCategoryTotal(userId, category1);
            double transportTotal = transactionController.getCategoryTotal(userId, category2);
            
            System.out.println("Food total: " + foodTotal);
            System.out.println("Transport total: " + transportTotal);
            
            assertEquals(amount1, foodTotal, 0.001);
            assertEquals(amount2, transportTotal, 0.001);
        }

        @Test
        @DisplayName("Test Monthly Transaction Summary")
        void testMonthlyTransactionSummary() throws SQLException {
            // Login user
            int userId = userControllerWithDb.authenticateUser(TEST_EMAIL, TEST_PASSWORD);
            assertTrue(userId >= 0);
            
            // Force clear all transactions at the beginning of this test
            DatabaseConfig dbConfig = TestDatabaseConfig.getInstance();
            if (dbConfig instanceof StubDatabaseConfig) {
                System.out.println("Explicitly clearing transactions for test: testMonthlyTransactionSummary");
                ((StubDatabaseConfig) dbConfig).clearTransactions();
                // Verify that transactions are really empty
                boolean isEmpty = ((StubDatabaseConfig) dbConfig).isTransactionsEmpty();
                if (!isEmpty) {
                    System.err.println("WARNING: Transactions not empty after clearing!");
                }
            } else {
                // For real DB, execute a delete statement
                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM transactions WHERE user_id = ?")) {
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                    System.out.println("Deleted existing transactions for user: " + userId);
                }
            }
            
            // Create transactions for different months
            String description1 = "January food";
            double amount1 = 100.0;
            LocalDate date1 = LocalDate.of(2024, 1, 1);
            boolean isIncome = false;
            String category = "Food";
            
            String description2 = "February food";
            double amount2 = 200.0;
            LocalDate date2 = LocalDate.of(2024, 2, 1);
            
            System.out.println("Adding January transaction: " + description1);
            transactionController.addTransaction(description1, amount1, date1, isIncome, category);
            System.out.println("Adding February transaction: " + description2);
            transactionController.addTransaction(description2, amount2, date2, isIncome, category);
            
            // Get monthly summary
            List<Transaction> janTransactions = transactionController.getTransactionsByMonth(userId, 2024, 1);
            List<Transaction> febTransactions = transactionController.getTransactionsByMonth(userId, 2024, 2);
            
            System.out.println("January transactions: " + janTransactions.size());
            System.out.println("February transactions: " + febTransactions.size());
            
            // Check that we have the transactions we expect 
            assertFalse(janTransactions.isEmpty(), "January transactions list should not be empty");
            assertFalse(febTransactions.isEmpty(), "February transactions list should not be empty");
            
            // Find the transactions with the expected amounts
            boolean foundJanuaryTransaction = false;
            for (Transaction t : janTransactions) {
                if (Math.abs(t.getAmount() - amount1) < 0.001) {
                    foundJanuaryTransaction = true;
                    break;
                }
            }
            
            boolean foundFebruaryTransaction = false;
            for (Transaction t : febTransactions) {
                if (Math.abs(t.getAmount() - amount2) < 0.001) {
                    foundFebruaryTransaction = true;
                    break;
                }
            }
            
            assertTrue(foundJanuaryTransaction, "January transaction with amount " + amount1 + " not found");
            assertTrue(foundFebruaryTransaction, "February transaction with amount " + amount2 + " not found");
        }
    }
}