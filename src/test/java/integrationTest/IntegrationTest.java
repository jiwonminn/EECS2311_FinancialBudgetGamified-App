package integrationTest;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import controller.AnalyticsController;
import controller.TransactionController;
import model.Transaction;
import database.DatabaseManager;


class IntegrationTest {
    private TransactionController transactionController;
    private AnalyticsController analyticsController;
    private static final int TEST_USER_ID = 1; //user Id
    private static final LocalDate TEST_DATE = LocalDate.now();
    
    @BeforeEach
    void setUp() throws SQLException {
        // Initialize controllers
        transactionController = new TransactionController();
        transactionController.setUserId(TEST_USER_ID);
        analyticsController = new AnalyticsController(TEST_USER_ID);
        
        // Clean up any existing test data
        cleanupTestData();
    }
    
    @AfterEach
    void tearDown() throws SQLException {
        // Clean up test data after each test
        cleanupTestData();
    }
    
    private void cleanupTestData() throws SQLException {
        // Delete all transactions for the test user
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement("DELETE FROM transactions WHERE user_id = ?")) {
            stmt.setInt(1, TEST_USER_ID);
            stmt.executeUpdate();
        }
    }
    
    @Test
    void testAddTransactionAndAnalytics() throws SQLException {
        // Add test transactions using the static method
        boolean salaryAdded = TransactionController.addTransaction(
            TEST_USER_ID, 
            TEST_DATE.toString(), 
            "Salary", 
            "Income", 
            "Income", 
            5000.0
        );
        assertTrue(salaryAdded, "Salary transaction should be added successfully");

        boolean rentAdded = TransactionController.addTransaction(
            TEST_USER_ID, 
            TEST_DATE.toString(), 
            "Rent", 
            "Housing", 
            "Expense", 
            2000.0
        );
        assertTrue(rentAdded, "Rent transaction should be added successfully");

        boolean groceriesAdded = TransactionController.addTransaction(
            TEST_USER_ID, 
            TEST_DATE.toString(), 
            "Groceries", 
            "Food", 
            "Expense", 
            500.0
        );
        assertTrue(groceriesAdded, "Groceries transaction should be added successfully");
        
        // Test transaction retrieval
        List<Transaction> transactions = analyticsController.getTransactions();
        assertEquals(3, transactions.size(), "Should have 3 transactions");
        
        // Test total income calculation
        double totalIncome = analyticsController.getTotalIncome();
        assertEquals(5000.0, totalIncome, 0.01, "Total income should be 5000.0");
        
        // Test total expenses calculation
        double totalExpenses = analyticsController.getTotalExpense();
        assertEquals(2500.0, totalExpenses, 0.01, "Total expenses should be 2500.0");
        
        // Test current savings calculation
        double currentSavings = analyticsController.getCurrentSavings();
        assertEquals(2500.0, currentSavings, 0.01, "Current savings should be 2500.0");
        
        // Test expenses by category
        Map<String, Double> expensesByCategory = analyticsController.getExpensesByCategory();
        assertEquals(2000.0, expensesByCategory.get("Housing"), 0.01, "Housing expenses should be 2000.0");
        assertEquals(500.0, expensesByCategory.get("Food"), 0.01, "Food expenses should be 500.0");
    }
    
    @Test
    void testTransactionDateRangeAndAnalytics() throws SQLException {
        LocalDate yesterday = TEST_DATE.minusDays(1);
        LocalDate tomorrow = TEST_DATE.plusDays(1);
        
        // Add transactions on different dates using the static method
        assertTrue(TransactionController.addTransaction(
            TEST_USER_ID,
            yesterday.toString(),
            "Past Income",
            "Income",
            "Income",
            1000.0
        ), "Past income transaction should be added successfully");

        assertTrue(TransactionController.addTransaction(
            TEST_USER_ID,
            TEST_DATE.toString(),
            "Current Income",
            "Income",
            "Income",
            2000.0
        ), "Current income transaction should be added successfully");

        assertTrue(TransactionController.addTransaction(
            TEST_USER_ID,
            tomorrow.toString(),
            "Future Income",
            "Income",
            "Income",
            3000.0
        ), "Future income transaction should be added successfully");
        
        // Test income data by date
        Map<LocalDate, Double> incomeData = analyticsController.getIncomeData();
        assertEquals(1000.0, incomeData.get(yesterday), 0.01, "Yesterday's income should be 1000.0");
        assertEquals(2000.0, incomeData.get(TEST_DATE), 0.01, "Today's income should be 2000.0");
        assertEquals(3000.0, incomeData.get(tomorrow), 0.01, "Tomorrow's income should be 3000.0");
        
        // Test savings over time
        Map<LocalDate, Double> savingsOverTime = analyticsController.getSavingsOverTime();
        assertEquals(1000.0, savingsOverTime.get(yesterday), 0.01, "Yesterday's savings should be 1000.0");
        assertEquals(2000.0, savingsOverTime.get(TEST_DATE), 0.01, "Today's savings should be 2000.0");
        assertEquals(3000.0, savingsOverTime.get(tomorrow), 0.01, "Tomorrow's savings should be 3000.0");
    }
    
    @Test
    void testTransactionModificationAndAnalytics() throws SQLException {
        // Add initial transaction using the static method
        assertTrue(TransactionController.addTransaction(
            TEST_USER_ID,
            TEST_DATE.toString(),
            "Initial Expense",
            "Shopping",
            "Expense",
            1000.0
        ), "Initial expense transaction should be added successfully");
        
        // Verify initial state
        double initialExpense = analyticsController.getTotalExpense();
        assertEquals(1000.0, initialExpense, 0.01, "Initial expense should be 1000.0");
        
        // Add more transactions
        assertTrue(TransactionController.addTransaction(
            TEST_USER_ID,
            TEST_DATE.toString(),
            "Additional Expense",
            "Shopping",
            "Expense",
            500.0
        ), "Additional expense transaction should be added successfully");
        
        // Verify updated state
        double updatedExpense = analyticsController.getTotalExpense();
        assertEquals(1500.0, updatedExpense, 0.01, "Updated expense should be 1500.0");
        
        // Test category analysis
        Map<String, Double> expensesByCategory = analyticsController.getExpensesByCategory();
        assertEquals(1500.0, expensesByCategory.get("Shopping"), 0.01, "Shopping expenses should be 1500.0");
    }
    
    @Test
    void testNegativeAmounts() throws SQLException {
    	
        // Test negative income
        assertThrows(IllegalArgumentException.class, ()-> TransactionController.addTransaction(
                TEST_USER_ID,
                TEST_DATE.toString(),
                "Negative Expense",
                "Shopping",
                "Expense",
                -1000.0));

       //Negative
        assertThrows(IllegalArgumentException.class, ()-> TransactionController.addTransaction(
                TEST_USER_ID,
                TEST_DATE.toString(),
                "Negative Expense",
                "Shopping",
                "Expense",
                -500.0));

        // Add valid transactions to verify negative amounts didn't affect totals
        assertTrue(TransactionController.addTransaction(
            TEST_USER_ID,
            TEST_DATE.toString(),
            "Valid Income",
            "Income",
            "Income",
            1000.0
        ), "Valid income should be added successfully");

        assertTrue(TransactionController.addTransaction(
            TEST_USER_ID,
            TEST_DATE.toString(),
            "Valid Expense",
            "Shopping",
            "Expense",
            500.0
        ), "Valid expense should be added successfully");

        // Verify totals only include valid transactions
        double totalIncome = analyticsController.getTotalIncome();
        assertEquals(1000.0, totalIncome, 0.01, "Total income should only include valid positive amounts");

        double totalExpense = analyticsController.getTotalExpense();
        assertEquals(500.0, totalExpense, 0.01, "Total expense should only include valid positive amounts");

        // Verify transaction count
        List<Transaction> transactions = analyticsController.getTransactions();
        assertEquals(2, transactions.size(), "Should only have the valid transactions");
    }
} 