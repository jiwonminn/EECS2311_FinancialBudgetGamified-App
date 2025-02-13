package tests;

import static org.junit.jupiter.api.Assertions.*;
import model.Transaction;
import org.junit.jupiter.api.Test;

class TransactionTest {

	@Test
    void testTransactionGetters() {
        Transaction transaction = new Transaction("2025-02-08", "Groceries", -50.75);
        assertEquals("2025-02-08", transaction.getDate());
        assertEquals("Groceries", transaction.getDescription());
        assertEquals(-50.75, transaction.getAmount());
    }

    @Test
    void testTransactionToString() {
        Transaction transaction = new Transaction("2025-02-08", "Groceries", -50.75);
        assertEquals("Date: 2025-02-08, Description: Groceries, Amount: $-50.75", transaction.toString());
    }

}
