package unitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.Before;
import model.Transaction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionTest {
    private Transaction transaction;
    private LocalDate testDate;

    @Before
    public void setUp() {
        testDate = LocalDate.now();
        transaction = new Transaction("Test Transaction", 100.0, testDate, false, "Food");
    }

    @Test
    public void testTransactionConstructorWithoutCategory() {
        Transaction transactionNoCategory = new Transaction("Test", 50.0, testDate, true);
        assertEquals("Test", transactionNoCategory.getDescription());
        assertEquals(50.0, transactionNoCategory.getAmount(), 0.001);
        assertEquals(testDate, transactionNoCategory.getDate());
        assertTrue(transactionNoCategory.isIncome());
        assertEquals("Other", transactionNoCategory.getCategory());
    }

    @Test
    public void testNegativeAmount() {
        assertThrows(IllegalArgumentException.class,()->new Transaction("Negative Test", -75.0, testDate, false));
    }

    @Test
    public void testNullValues() {
        assertThrows(NullPointerException.class,()->new Transaction(null, 100.0, null, false, null));
    }

    @Test
    public void testZeroAmount() {
        Transaction zeroTransaction = new Transaction("Zero Amount", 0.0, testDate, false);
        assertEquals(0.0, zeroTransaction.getAmount(), 0.001);
    }

    @Test
    public void testLargeAmount() {
        double largeAmount = 999999999.99;
        Transaction largeTransaction = new Transaction("Large Amount", largeAmount, testDate, true);
        assertEquals(largeAmount, largeTransaction.getAmount(), 0.001);
    }

    @Test
    public void testDescriptionTrimming() {
        Transaction trimmedTransaction = new Transaction("  Spaces  ", 100.0, testDate, false);
        assertEquals("Spaces", trimmedTransaction.getDescription().trim());
    }

    @Test
    public void testCategoryValidation() {
        List<String> validCategories = new ArrayList<>();
        validCategories.add("Food");
        validCategories.add("Transport");
        validCategories.add("Entertainment");
        validCategories.add("Other");

        Transaction transaction = new Transaction("Test", 100.0, testDate, false, "Food");
        assertTrue(validCategories.contains(transaction.getCategory()));
    }

    @Test
    public void testTransactionEquality() {
        Transaction transaction1 = new Transaction("Same", 100.0, testDate, false, "Food");
        
        assertEquals(String.valueOf(100.0),transaction1.getAmount(), "The amount is not the same");
    }
}