package tests;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import controller.TransactionController;
import model.Transaction;
import org.junit.jupiter.api.BeforeEach;


class TransactionControllerTest {

	private TransactionController controller;

    @BeforeEach
    void setUp() {
        controller = new TransactionController();
        controller.addTransaction("2025-02-08", "Groceries", -50.75);
        controller.addTransaction("2025-02-07", "Salary", 1500.00);
        controller.addTransaction("2025-02-06", "Electricity Bill", -100.00);
        controller.addTransaction("2025-02-05", "Rent Payment", -900.00);
        controller.addTransaction("2025-02-04", "Dinner", -45.00);
    }

    @Test
    void testAddTransaction() {
        controller.addTransaction("2025-02-09", "Gas", -30.00);
        List<Transaction> transactions = controller.getTransactions();
        assertEquals(6, transactions.size());
        assertEquals("Gas", transactions.get(5).getDescription());
    }

    @Test
    void testSortByDate() {
        controller.sortTransactions("date");
        List<Transaction> transactions = controller.getTransactions();
        assertEquals("2025-02-04", transactions.get(0).getDate());
        assertEquals("2025-02-08", transactions.get(4).getDate());
    }

    @Test
    void testSortByAmount() {
        controller.sortTransactions("amount");
        List<Transaction> transactions = controller.getTransactions();
        assertEquals(-900.00, transactions.get(0).getAmount());
        assertEquals(1500.00, transactions.get(4).getAmount());
    }

    @Test
    void testSortByDescription() {
        controller.sortTransactions("description");
        List<Transaction> transactions = controller.getTransactions();
        assertEquals("Dinner", transactions.get(0).getDescription());
        assertEquals("Salary", transactions.get(4).getDescription());
    }

    @Test
    void testFilterByDateRange() {
        List<Transaction> result = controller.filterByDateRange("2025-02-06", "2025-02-08");
        assertEquals(3, result.size());
        assertEquals("Groceries", result.get(0).getDescription());
    }

    @Test
    void testFilterByDescription() {
        List<Transaction> result = controller.filterByDescription("Bill");
        assertEquals(1, result.size());
        assertEquals("Electricity Bill", result.get(0).getDescription());
    }

    @Test
    void testFilterNoMatch() {
        List<Transaction> result = controller.filterByDescription("Nonexistent");
        assertTrue(result.isEmpty());
    }

}
