package integrationTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import database.DatabaseConfig;
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

public class FinancialAppIntegrationTest {
    private static Connection connection;
    private static UserController userController;
    private static UserControllerWithDatabase userControllerWithDb;
    private static TransactionController transactionController;
    private static UserDao userDao;
    private static User testUser;
    private static final String TEST_PASSWORD = "testPassword123";
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_EMAIL = "testt12@example.com";
    private static final double INITIAL_BALANCE = 0.0;

    @BeforeAll
    public static void setUp() throws SQLException {
        // Use the test database configuration
        DatabaseConfig dbConfig = TestDatabaseConfig.getInstance();
        connection = dbConfig.getConnection();
        
        // Initialize DAOs and controllers
        userDao = new UserDaoImpl(connection);
        userControllerWithDb = new UserControllerWithDatabase();
        userController = new UserController(TEST_USERNAME, TEST_EMAIL, INITIAL_BALANCE);
        transactionController = new TransactionController(connection);
        
        // Create a test user
        int userId = userControllerWithDb.registerUser(TEST_EMAIL, TEST_PASSWORD);
        testUser = userDao.findUserById(userId);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up test data
        if (testUser != null) {
            // Delete user from database
            boolean deleted = userDao.deleteUser(testUser.getId());
            if (!deleted) {
                System.err.println("Failed to delete test user with ID: " + testUser.getId());
            }
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void testUserRegistration() throws SQLException {
        // Test registering a new user
        int userId = userControllerWithDb.registerUser("newusertest123@example.com", "password123");
        assertTrue(userId >= 0);
        
       
        
        // Clean up
        userDao.deleteUser(userId);
    }

    @Test
    public void testDuplicateUserRegistration() {
        // Test registering a user with an existing email
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
        assertNotNull(authenticatedUser);
        assertEquals(TEST_EMAIL, authenticatedUser.getEmail());
        
        // Test failed authentication
        int failedUserId = userControllerWithDb.authenticateUser(TEST_EMAIL, "wrongpassword");
        assertEquals(-1, failedUserId);
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
        // Test adding points
        transactionController.awardXpForTransaction(testUser.getId());
        User updatedUser = userDao.findUserById(testUser.getId());
        assertEquals(100, updatedUser.getPoints());
        
        // Test updating balance
        transactionController.updateBalance(testUser.getId(), 50.0);
        updatedUser = userDao.findUserById(testUser.getId());
        assertEquals(50.0, updatedUser.getBalance());
        
        // Reset user state for other tests
        transactionController.updateBalance(testUser.getId(), 0.0);
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
            
            // Create initial transaction
            String description = "Initial transaction";
            double amount = 100.0;
            LocalDate date = LocalDate.now();
            boolean isIncome = false;
            String category = "Food";
            
            transactionController.addTransaction(description, amount, date, isIncome, category);
            
            // Get the transaction ID from the database
            List<Transaction> transactions = transactionController.getTransactions();
            assertFalse(transactions.isEmpty());
            int transactionId = transactions.get(0).getId();
            
            // Delete the old transaction
            boolean deleted = TransactionController.deleteTransaction(transactionId);
            assertTrue(deleted);
            
            // Create updated transaction
            String updatedDescription = "Updated transaction";
            double updatedAmount = 150.0;
            String updatedCategory = "Entertainment";
            
            transactionController.addTransaction(updatedDescription, updatedAmount, date, isIncome, updatedCategory);
            
            // Verify update
            transactions = transactionController.getTransactions();
            assertFalse(transactions.isEmpty());
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
            boolean deleted = TransactionController.deleteTransaction(transactionId);
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

        @Test
        @DisplayName("Test Budget Exceeded Warning")
        void testBudgetExceededWarning() throws SQLException {
            // Login user
            int userId = userControllerWithDb.authenticateUser(TEST_EMAIL, TEST_PASSWORD);
            assertTrue(userId > 0);
            
            // Set budget
            userController.setMonthlyBudget(userId, 1000.0);
            
            // Create transactions exceeding budget
            String description1 = "Food transaction";
            double amount1 = 600.0;
            LocalDate date = LocalDate.now();
            boolean isIncome = false;
            String category1 = "Food";
            
            String description2 = "Transport transaction";
            double amount2 = 500.0;
            String category2 = "Transport";
            
            transactionController.addTransaction(description1, amount1, date, isIncome, category1);
            transactionController.addTransaction(description2, amount2, date, isIncome, category2);
            
            // Check if budget exceeded warning is triggered
            boolean isBudgetExceeded = userController.isBudgetExceeded(userId);
            assertTrue(isBudgetExceeded);
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
            
            // Create multiple transactions in different categories
            String description1 = "Food purchase";
            double amount1 = 50.0;
            LocalDate date = LocalDate.now();
            boolean isIncome = false;
            String category1 = "Food";
            
            String description2 = "Transport fare";
            double amount2 = 30.0;
            String category2 = "Transport";
            
            transactionController.addTransaction(description1, amount1, date, isIncome, category1);
            transactionController.addTransaction(description2, amount2, date, isIncome, category2);
            
            // Verify category statistics
            double foodTotal = transactionController.getCategoryTotal(userId, category1);
            double transportTotal = transactionController.getCategoryTotal(userId, category2);
            
            assertEquals(amount1, foodTotal);
            assertEquals(amount2, transportTotal);
        }

        @Test
        @DisplayName("Test Monthly Transaction Summary")
        void testMonthlyTransactionSummary() throws SQLException {
            // Login user
            int userId = userControllerWithDb.authenticateUser(TEST_EMAIL, TEST_PASSWORD);
            assertTrue(userId > 0);
            
            // Create transactions for different months
            String description1 = "January food";
            double amount1 = 100.0;
            LocalDate date1 = LocalDate.of(2024, 1, 1);
            boolean isIncome = false;
            String category = "Food";
            
            String description2 = "February food";
            double amount2 = 200.0;
            LocalDate date2 = LocalDate.of(2024, 2, 1);
            
            transactionController.addTransaction(description1, amount1, date1, isIncome, category);
            transactionController.addTransaction(description2, amount2, date2, isIncome, category);
            
            // Get monthly summary
            List<Transaction> janTransactions = transactionController.getTransactionsByMonth(userId, 2024, 1);
            List<Transaction> febTransactions = transactionController.getTransactionsByMonth(userId, 2024, 2);
            
            assertEquals(1, janTransactions.size());
            assertEquals(1, febTransactions.size());
            assertEquals(amount1, janTransactions.get(0).getAmount());
            assertEquals(amount2, febTransactions.get(0).getAmount());
        }
    }
} 