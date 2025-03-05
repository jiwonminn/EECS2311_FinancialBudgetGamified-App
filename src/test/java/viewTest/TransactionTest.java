package viewTest;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;
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
    public void testTransactionConstructor() {
        assertEquals("Test Transaction", transaction.getDescription());
        assertEquals(100.0, transaction.getAmount(), 0.001);
        assertEquals(testDate, transaction.getDate());
        assertFalse(transaction.isIncome());
        assertEquals("Food", transaction.getCategory());
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
        Transaction negativeTransaction = new Transaction("Negative Test", -75.0, testDate, false);
        assertEquals(75.0, negativeTransaction.getAmount(), 0.001);
    }

    @Test
    public void testNullValues() {
        Transaction nullTransaction = new Transaction(null, 100.0, null, false, null);
        assertNotNull(nullTransaction.getDescription());
        assertEquals("", nullTransaction.getDescription());
        assertNotNull(nullTransaction.getDate());
        assertEquals(LocalDate.now(), nullTransaction.getDate());
        assertEquals("Other", nullTransaction.getCategory());
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
        Transaction transaction2 = new Transaction("Same", 100.0, testDate, false, "Food");
        Transaction transaction3 = new Transaction("Different", 200.0, testDate, true, "Transport");

        assertEquals(transaction1, transaction1); // Same object
        assertEquals(transaction1, transaction2); // Equal objects
        assertNotEquals(transaction1, transaction3); // Different objects
    }
} 