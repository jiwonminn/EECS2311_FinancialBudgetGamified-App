package unitTests;

import controller.BudgetController;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Budget;
import model.Transaction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// A simple fake TransactionDao for BudgetController.
// (Assuming the interface defines getTransactionsByUserId.)
class FakeTransactionDao implements database.dao.TransactionDao {
    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    @Override
    public List<Transaction> getTransactionsByUserId(int userId) {
        // For simplicity, return all transactions regardless of user.
        return transactions;
    }

    @Override
    public void addTransaction(int userId, LocalDate date, double amount, String description, String type, String category) throws SQLException {

    }

    @Override
    public boolean updateTransaction(Transaction transaction) throws SQLException {
        return false;
    }

    @Override
    public boolean deleteTransaction(int transactionId) throws SQLException {
        return false;
    }

    @Override
    public int getTransactionCountForUser(int userId) throws SQLException {
        return 0;
    }

    @Override
    public int getTransactionCountForDay(int userId, LocalDate date) throws SQLException {
        return 0;
    }
}

// A simple fake Transaction for testing.
// (Assumes model.Transaction defines isIncome(), getAmount(), and getDate().)
class FakeTransaction extends Transaction {
    private double amount;
    private boolean income;
    private LocalDate date;

    public FakeTransaction(double amount, boolean income, LocalDate date) {
        super("", amount, date, income, "");
        this.amount = amount;
        this.income = income;
        this.date = date;
    }

    @Override
    public boolean isIncome() {
        return income;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }
}

public class BudgetControllerTest {

    private int testUserId = 1;
    private Budget testBudget = new Budget(1000.0, 300.0);
    private BudgetController controller;
    private FakeTransactionDao fakeTransDao = new FakeTransactionDao();

    // Helper method to set private fields via reflection.
    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    @org.junit.jupiter.api.BeforeEach
    public void setUp() throws Exception {
        // Create a BudgetController using a test budget.
        controller = new BudgetController(testUserId, testBudget);
        // Replace its transactionDao with our fake implementation.
        setPrivateField(controller, "transactionDao", fakeTransDao);
        // Clear any previously added fake transactions.
        fakeTransDao = new FakeTransactionDao();
        setPrivateField(controller, "transactionDao", fakeTransDao);
    }

    @Test
    public void testGetMonthlySpending() throws Exception {
        LocalDate today = LocalDate.now();
        // Add an expense in the current month.
        fakeTransDao.addTransaction(new FakeTransaction(50.0, false, today));
        // Add an expense outside the current month.
        fakeTransDao.addTransaction(new FakeTransaction(30.0, false, today.minusMonths(1)));
        double spending = controller.getMonthlySpending();
        assertEquals(50.0, spending, 0.001, "Monthly spending should equal expense in current month");
    }

    @Test
    public void testGetWeeklySpending() throws Exception {
        LocalDate today = LocalDate.now();
        // Add an expense in the current week.
        fakeTransDao.addTransaction(new FakeTransaction(40.0, false, today));
        // Add an expense outside the current week.
        fakeTransDao.addTransaction(new FakeTransaction(20.0, false, today.minusWeeks(2)));
        double spending = controller.getWeeklySpending();
        assertEquals(40.0, spending, 0.001, "Weekly spending should equal expense in current week");
    }

    @Test
    public void testIsOverWeeklyBudget() throws Exception {
        LocalDate today = LocalDate.now();
        // Add an expense that exceeds the weekly limit.
        fakeTransDao.addTransaction(new FakeTransaction(350.0, false, today));
        boolean overWeekly = controller.isOverWeeklyBudget();
        assertTrue(overWeekly, "Should be over weekly budget");
    }

    @Test
    public void testIsOverMonthlyBudget() throws Exception {
        LocalDate today = LocalDate.now();
        // Add an expense that exceeds the monthly limit.
        fakeTransDao.addTransaction(new FakeTransaction(1200.0, false, today));
        boolean overMonthly = controller.isOverMonthlyBudget();
        assertTrue(overMonthly, "Should be over monthly budget");
    }

    @Test
    public void testUpdateAndGetBudget() {
        controller.updateBudget(2000.0, 500.0);
        Budget updated = controller.getBudget();
        assertEquals(2000.0, updated.getMonthlyLimit(), 0.001, "Monthly limit should be updated");
        assertEquals(500.0, updated.getWeeklyLimit(), 0.001, "Weekly limit should be updated");
    }
}

