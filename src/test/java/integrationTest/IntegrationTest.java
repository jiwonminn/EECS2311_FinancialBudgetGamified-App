package integrationTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import database.DatabaseManager;
import model.Transaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class IntegrationTest {
    private DatabaseManager dbManager;
    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        // Get database connection
        connection = DatabaseManager.getConnection();

        // Create test table
        createTestTable();

        // Clear any existing data
        clearTestData();
    }

    @After
    public void tearDown() throws SQLException {
        // Clean up test data
        clearTestData();
    }

    private void createTestTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INT AUTO_INCREMENT PRIMARY KEY,
                description VARCHAR(255) NOT NULL,
                amount DECIMAL(10,2) NOT NULL,
                date DATE NOT NULL,
                is_income BOOLEAN NOT NULL,
                category VARCHAR(50) NOT NULL
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    private void clearTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM transactions");
        }
    }

    @Test
    public void testDatabaseConnection() {
        assertNotNull("Database connection should not be null", connection);
        try {
            assertFalse("Database connection should be valid", connection.isClosed());
        } catch (SQLException e) {
            fail("Database connection test failed: " + e.getMessage());
        }
    }


    @Test
    public void testMultipleTransactions() throws SQLException {
        // Create multiple test transactions
        Transaction transaction1 = new Transaction("Test 1", 100.0, LocalDate.now(), false, "Category1");
        Transaction transaction2 = new Transaction("Test 2", 200.0, LocalDate.now(), true, "Category2");
        Transaction transaction3 = new Transaction("Test 3", 300.0, LocalDate.now(), false, "Category1");

        createTestTransaction(transaction1);
        createTestTransaction(transaction2);
        createTestTransaction(transaction3);

        // Test retrieving all transactions
        List<Transaction> allTransactions = getAllTestTransactions();
        assertEquals("Should have 3 transactions", 3, allTransactions.size());

        // Test retrieving by category
        List<Transaction> category1Transactions = getTransactionsByCategory("Category1");
        assertEquals("Should have 2 transactions in Category1", 2, category1Transactions.size());
    }

    @Test
    public void testDateRangeQuery() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);

        // Create transactions with different dates
        Transaction pastTransaction = new Transaction("Past", 100.0, yesterday, false, "Test");
        Transaction presentTransaction = new Transaction("Present", 200.0, today, false, "Test");
        Transaction futureTransaction = new Transaction("Future", 300.0, tomorrow, false, "Test");

        createTestTransaction(pastTransaction);
        createTestTransaction(presentTransaction);
        createTestTransaction(futureTransaction);

        // Test date range query
        List<Transaction> dateRangeTransactions = getTransactionsByDateRange(yesterday, today);
        assertEquals("Should have 2 transactions in date range", 2, dateRangeTransactions.size());
    }


    @Test
    public void anothertest() {

    }




    // Helper methods to perform database operations
    private int createTestTransaction(Transaction transaction) throws SQLException {
        String sql = """
            INSERT INTO transactions (description, amount, date, is_income, category)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (var pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, transaction.getDescription());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setDate(3, java.sql.Date.valueOf(transaction.getDate()));
            pstmt.setBoolean(4, transaction.isIncome());
            pstmt.setString(5, transaction.getCategory());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }

            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating transaction failed, no ID obtained.");
                }
            }
        }
    }

    private Transaction getTestTransaction(int id) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE id = ?";

        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Transaction(
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getDate("date").toLocalDate(),
                        rs.getBoolean("is_income"),
                        rs.getString("category")
                    );
                } else {
                    throw new SQLException("Transaction not found with id: " + id);
                }
            }
        }
    }

    private boolean updateTestTransaction(int id, Transaction transaction) throws SQLException {
        String sql = """
            UPDATE transactions
            SET description = ?, amount = ?, date = ?, is_income = ?, category = ?
            WHERE id = ?
        """;

        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getDescription());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setDate(3, java.sql.Date.valueOf(transaction.getDate()));
            pstmt.setBoolean(4, transaction.isIncome());
            pstmt.setString(5, transaction.getCategory());
            pstmt.setInt(6, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    private boolean deleteTestTransaction(int id) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    private List<Transaction> getAllTestTransactions() throws SQLException {
        String sql = "SELECT * FROM transactions ORDER BY date DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (var stmt = connection.createStatement();
             var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getString("description"),
                    rs.getDouble("amount"),
                    rs.getDate("date").toLocalDate(),
                    rs.getBoolean("is_income"),
                    rs.getString("category")
                ));
            }
        }
        return transactions;
    }

    private List<Transaction> getTransactionsByCategory(String category) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE category = ? ORDER BY date DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category);

            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getDate("date").toLocalDate(),
                        rs.getBoolean("is_income"),
                        rs.getString("category")
                    ));
                }
            }
        }
        return transactions;
    }

    private List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(startDate));
            pstmt.setDate(2, java.sql.Date.valueOf(endDate));

            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getDate("date").toLocalDate(),
                        rs.getBoolean("is_income"),
                        rs.getString("category")
                    ));
                }
            }
        }
        return transactions;
    }


}