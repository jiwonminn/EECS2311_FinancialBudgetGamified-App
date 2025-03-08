package unitTests;

import model.Transaction;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import controller.AnalyticsController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalyticsControllerTest {

    private AnalyticsController analyticsController;
    
    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // Setup mock behavior
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        
        // Create test data in the mock ResultSet
        when(mockResultSet.next())
            .thenReturn(true, true, true, false); // Return true 3 times for 3 transactions, then false
            
        // Mock the transaction data
        when(mockResultSet.getString("description"))
            .thenReturn("Groceries", "Salary", "Rent");
        when(mockResultSet.getDouble("amount"))
            .thenReturn(100.0, 5000.0, 1500.0);
        when(mockResultSet.getDate("date"))
            .thenReturn(java.sql.Date.valueOf(LocalDate.now()),
                    java.sql.Date.valueOf(LocalDate.now()),
                    java.sql.Date.valueOf(LocalDate.now()));
        when(mockResultSet.getBoolean("is_income"))
            .thenReturn(false, true, false);
        when(mockResultSet.getString("category"))
            .thenReturn("Food", "Income", "Housing");
            
        // Create analytics controller with mock connection
        analyticsController = new AnalyticsController() {
            @Override
            protected Connection getConnection() {
                return mockConnection;
            }
        };
    }

    @Test
    void testGetTransactions() {
        List<Transaction> transactions = analyticsController.getTransactions();
        
        assertNotNull(transactions);
        assertEquals(3, transactions.size());
        
        // Verify first transaction
        Transaction firstTransaction = transactions.get(0);
        assertEquals("Groceries", firstTransaction.getDescription());
        assertEquals(100.0, firstTransaction.getAmount());
        assertEquals("Food", firstTransaction.getCategory());
        assertFalse(firstTransaction.isIncome());
        
        // Verify second transaction
        Transaction secondTransaction = transactions.get(1);
        assertEquals("Salary", secondTransaction.getDescription());
        assertEquals(5000.0, secondTransaction.getAmount());
        assertEquals("Income", secondTransaction.getCategory());
        assertTrue(secondTransaction.isIncome());
    }

    @Test
    void testGetIncomeData() {
        Map<LocalDate, Double> incomeData = analyticsController.getIncomeData();
        
        assertNotNull(incomeData);
        assertFalse(incomeData.isEmpty());
        
        // Should only contain income transactions
        double totalIncome = incomeData.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(5000.0, totalIncome);
    }

    @Test
    void testGetExpenseData() {
        Map<LocalDate, Double> expenseData = analyticsController.getExpenseData();
        
        assertNotNull(expenseData);
        assertFalse(expenseData.isEmpty());
        
        // Should only contain expense transactions
        double totalExpenses = expenseData.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(1600.0, totalExpenses); // 100 (groceries) + 1500 (rent)
    }

    @Test
    void testGetTotalIncome() {
        double totalIncome = analyticsController.getTotalIncome();
        assertEquals(5000.0, totalIncome);
    }

    @Test
    void testGetTotalExpense() {
        double totalExpense = analyticsController.getTotalExpense();
        assertEquals(1600.0, totalExpense);
    }

    @Test
    void testGetCurrentSavings() {
        double currentSavings = analyticsController.getCurrentSavings();
        assertEquals(3400.0, currentSavings); // 5000 - 1600
    }

    @Test
    void testGetSavingsOverTime() {
        Map<LocalDate, Double> savingsOverTime = analyticsController.getSavingsOverTime();
        
        assertNotNull(savingsOverTime);
        assertFalse(savingsOverTime.isEmpty());
        
        // For our test data, all transactions are on the same date
        LocalDate today = LocalDate.now();
        assertTrue(savingsOverTime.containsKey(today));
        assertEquals(3400.0, savingsOverTime.get(today)); // 5000 - 1600
    }

    @Test
    void testGetExpensesByCategory() {
        Map<String, Double> expensesByCategory = analyticsController.getExpensesByCategory();
        
        assertNotNull(expensesByCategory);
        assertFalse(expensesByCategory.isEmpty());
        
        assertEquals(100.0, expensesByCategory.get("Food"));
        assertEquals(1500.0, expensesByCategory.get("Housing"));
    }

    @Test
    void testHandleNullCategory() {
        // Setup mock to return null category
        when(mockResultSet.getString("category"))
            .thenReturn(null, "Income", "Housing");
            
        List<Transaction> transactions = analyticsController.getTransactions();
        
        assertNotNull(transactions);
        assertEquals(3, transactions.size());
        assertEquals("Other", transactions.get(0).getCategory());
    }

    @Test
    void testHandleDatabaseError() throws Exception {
        // Setup mock to throw exception
        when(mockStatement.executeQuery(anyString()))
            .thenThrow(new java.sql.SQLException("Database error"));
            
        List<Transaction> transactions = analyticsController.getTransactions();
        
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }
} 