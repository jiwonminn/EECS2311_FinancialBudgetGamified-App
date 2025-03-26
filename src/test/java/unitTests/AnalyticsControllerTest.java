package unitTests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.AnalyticsController;
import model.Transaction;

class AnalyticsControllerTest {
    private AnalyticsController analyticsController;
    private List<Transaction> testTransactions;
    private static final int TEST_USER_ID = 1;
    private static final LocalDate TEST_DATE = LocalDate.now();

    @BeforeEach
    void setUp() {
        analyticsController = new TestAnalyticsController(TEST_USER_ID);
        setupTestData();
    }

    private void setupTestData() {
        testTransactions = new ArrayList<>();

        // Add some test transactions
        // Income transactions
        testTransactions.add(new Transaction("Salary", 5000.0, TEST_DATE, true, "Income"));
        testTransactions.add(new Transaction("Bonus", 1000.0, TEST_DATE, true, "Income"));

        // Expense transactions
        testTransactions.add(new Transaction("Rent", 2000.0, TEST_DATE, false, "Housing"));
        testTransactions.add(new Transaction("Groceries", 500.0, TEST_DATE, false, "Food"));
        testTransactions.add(new Transaction("Internet", 100.0, TEST_DATE, false, "Utilities"));

        // Transactions on different dates
        testTransactions.add(new Transaction("Past Income", 1500.0, TEST_DATE.minusDays(1), true, "Income"));
        testTransactions.add(new Transaction("Future Expense", 300.0, TEST_DATE.plusDays(1), false, "Entertainment"));
    }

    // Test class that overrides database access with in-memory data
    private class TestAnalyticsController extends AnalyticsController {
        public TestAnalyticsController(int userId) {
            super(userId);
        }

        @Override
        public List<Transaction> getTransactions() {
            return testTransactions;
        }
    }

    @Test
    void testGetTotalIncome() throws SQLException {
        double expectedIncome = 7500.0; // 5000 + 1000 + 1500
        assertEquals(expectedIncome, analyticsController.getTotalIncome(), 0.01,
                "Total income should be sum of all income transactions");
    }

    @Test
    void testGetTotalExpense() throws SQLException {
        double expectedExpense = 2900.0; // 2000 + 500 + 100 + 300
        assertEquals(expectedExpense, analyticsController.getTotalExpense(), 0.01,
                "Total expense should be sum of all expense transactions");
    }

    @Test
    void testGetCurrentSavings() throws SQLException {
        double expectedSavings = 4600.0; // 7500 - 2900
        assertEquals(expectedSavings, analyticsController.getCurrentSavings(), 0.01,
                "Current savings should be total income minus total expenses");
    }

    @Test
    void testGetExpensesByCategory() throws SQLException {
        Map<String, Double> expensesByCategory = analyticsController.getExpensesByCategory();

        assertEquals(2000.0, expensesByCategory.get("Housing"), 0.01,
                "Housing expenses should match");
        assertEquals(500.0, expensesByCategory.get("Food"), 0.01,
                "Food expenses should match");
        assertEquals(100.0, expensesByCategory.get("Utilities"), 0.01,
                "Utilities expenses should match");
        assertEquals(300.0, expensesByCategory.get("Entertainment"), 0.01,
                "Entertainment expenses should match");
    }

    @Test
    void testGetIncomeData() throws SQLException {
        Map<LocalDate, Double> incomeData = analyticsController.getIncomeData();

        assertEquals(1500.0, incomeData.get(TEST_DATE.minusDays(1)), 0.01,
                "Past day income should match");
        assertEquals(6000.0, incomeData.get(TEST_DATE), 0.01,
                "Current day income should match");
        assertNull(incomeData.get(TEST_DATE.plusDays(1)),
                "Future day should have no income");
    }

    @Test
    void testGetSavingsOverTime() throws SQLException {
        Map<LocalDate, Double> savingsOverTime = analyticsController.getSavingsOverTime();

        assertEquals(1500.0, savingsOverTime.get(TEST_DATE.minusDays(1)), 0.01,
                "Past day savings should be income only");
        assertEquals(3400.0, savingsOverTime.get(TEST_DATE), 0.01,
                "Current day savings should be income minus expenses");
        assertEquals(-300.0, savingsOverTime.get(TEST_DATE.plusDays(1)), 0.01,
                "Future day savings should be negative expenses");
    }

    @Test
    void testEmptyTransactions() throws SQLException {
        testTransactions.clear();

        assertEquals(0.0, analyticsController.getTotalIncome(), 0.01,
                "Total income should be 0 with no transactions");
        assertEquals(0.0, analyticsController.getTotalExpense(), 0.01,
                "Total expense should be 0 with no transactions");
        assertEquals(0.0, analyticsController.getCurrentSavings(), 0.01,
                "Current savings should be 0 with no transactions");
        assertTrue(analyticsController.getExpensesByCategory().isEmpty(),
                "Expenses by category should be empty with no transactions");
        assertTrue(analyticsController.getIncomeData().isEmpty(),
                "Income data should be empty with no transactions");
        assertTrue(analyticsController.getSavingsOverTime().isEmpty(),
                "Savings over time should be empty with no transactions");
    }

    @Test
    void testTransactionsOnSameDay() throws SQLException {
        testTransactions.clear();
        testTransactions.add(new Transaction("Income 1", 1000.0, TEST_DATE, true, "Income"));
        testTransactions.add(new Transaction("Income 2", 500.0, TEST_DATE, true, "Income"));
        testTransactions.add(new Transaction("Expense 1", 300.0, TEST_DATE, false, "Food"));
        testTransactions.add(new Transaction("Expense 2", 200.0, TEST_DATE, false, "Food"));

        Map<LocalDate, Double> incomeData = analyticsController.getIncomeData();
        assertEquals(1500.0, incomeData.get(TEST_DATE), 0.01,
                "Same day incomes should be summed");

        Map<String, Double> expensesByCategory = analyticsController.getExpensesByCategory();
        assertEquals(500.0, expensesByCategory.get("Food"), 0.01,
                "Same category expenses should be summed");
    }

//    @Test
//    void testNegativeAmounts() {
//        testTransactions.clear();
//        assertThrows(IllegalArgumentException.class, () -> {
//            testTransactions.add(new Transaction("Negative Income", -1000.0, TEST_DATE, true, "Income"));
//        }, "Should not allow negative income amount");
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            testTransactions.add(new Transaction("Negative Expense", -500.0, TEST_DATE, false, "Food"));
//        }, "Should not allow negative expense amount");
//    }
}