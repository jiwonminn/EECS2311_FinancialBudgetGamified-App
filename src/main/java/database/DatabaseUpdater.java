package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class handles database schema updates
 */
public class DatabaseUpdater {
    
    /**
     * Initializes the database schema
     */
    public static void initializeDatabase() {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            createUsersTable(conn);
            createTransactionsTable(conn);
            createQuizTable(conn);
            createUserGoalsTable(conn);
            createDefaultUser(conn);
            updateTransactionsTable(conn);
                } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
                    e.printStackTrace();
        }
    }
    
    /**
     * Adds the 'category' column to the transactions table if it doesn't exist
     */
    public static void updateTransactionsTable(Connection conn) throws SQLException {
        if (conn == null) {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                throw new SQLException("Failed to get database connection");
            }
        }
        
        DatabaseMetaData dbm = conn.getMetaData();
        ResultSet rs = dbm.getColumns(null, null, "transactions", "category");
        
        // If the category column doesn't exist, add it
        if (!rs.next()) {
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                stmt.executeUpdate("ALTER TABLE transactions ADD COLUMN category VARCHAR(50) DEFAULT 'Uncategorized'");
                System.out.println("Added 'category' column to transactions table");
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
        rs.close();
    }
    
    /**
     * Creates the users table if it doesn't exist
     */
    private static void createUsersTable(Connection conn) throws SQLException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100) UNIQUE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(sql);
            System.out.println("Created or verified users table");
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    /**
     * Creates the transactions table if it doesn't exist
     */
    private static void createTransactionsTable(Connection conn) throws SQLException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id SERIAL PRIMARY KEY," +
                    "user_id INTEGER REFERENCES users(id)," +
                    "amount DECIMAL(10,2) NOT NULL," +
                    "description VARCHAR(255)," +
                    "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "type VARCHAR(10) NOT NULL" +
                    ")";
            stmt.executeUpdate(sql);
            System.out.println("Created or verified transactions table");
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    /**
     * Creates the quiz table and adds sample questions if it's empty
     */
    private static void createQuizTable(Connection conn) throws SQLException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            // Create the quiz table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS quiz_questions (" +
                    "id SERIAL PRIMARY KEY," +
                    "question TEXT NOT NULL," +
                    "option_a TEXT NOT NULL," +
                    "option_b TEXT NOT NULL," +
                    "option_c TEXT NOT NULL," +
                    "option_d TEXT NOT NULL," +
                    "correct_answer CHAR(1) NOT NULL," +
                    "explanation TEXT," +
                    "difficulty INTEGER DEFAULT 1" +
                    ")";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Created or verified quiz_questions table");
            
            // Check if there are already questions in the table
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM quiz_questions");
            rs.next();
            int questionCount = rs.getInt(1);
            rs.close();
            
            // If there are no questions, add some sample ones
            if (questionCount == 0) {
                addSampleQuizQuestions(conn);
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    /**
     * Adds sample quiz questions to the database
     */
    private static void addSampleQuizQuestions(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String insertSQL = "INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, explanation, difficulty) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSQL);
            
            // Sample Question 1
            pstmt.setString(1, "What is the 50/30/20 budgeting rule?");
            pstmt.setString(2, "50% needs, 30% wants, 20% savings");
            pstmt.setString(3, "50% savings, 30% needs, 20% wants");
            pstmt.setString(4, "50% wants, 30% savings, 20% needs");
            pstmt.setString(5, "50% needs, 30% savings, 20% wants");
            pstmt.setString(6, "A");
            pstmt.setString(7, "The 50/30/20 rule suggests spending 50% of your income on needs, 30% on wants, and 20% on savings.");
            pstmt.setInt(8, 1);
            pstmt.executeUpdate();
            
            // Sample Question 2
            pstmt.setString(1, "What is compound interest?");
            pstmt.setString(2, "Interest paid only on the principal amount");
            pstmt.setString(3, "Interest paid on both the principal and accumulated interest");
            pstmt.setString(4, "A fixed interest rate that never changes");
            pstmt.setString(5, "A penalty for late payments");
            pstmt.setString(6, "B");
            pstmt.setString(7, "Compound interest is calculated on both the initial principal and the accumulated interest, making your money grow faster over time.");
            pstmt.setInt(8, 2);
            pstmt.executeUpdate();
            
            // Sample Question 3
            pstmt.setString(1, "Which of these is generally considered a good debt?");
            pstmt.setString(2, "Credit card debt");
            pstmt.setString(3, "Payday loans");
            pstmt.setString(4, "Student loans for education");
            pstmt.setString(5, "Auto loans for luxury cars");
            pstmt.setString(6, "C");
            pstmt.setString(7, "Student loans are often considered 'good debt' because education typically increases your earning potential and the interest rates are usually lower.");
            pstmt.setInt(8, 2);
            pstmt.executeUpdate();
            
            // Sample Question 4
            pstmt.setString(1, "What is an emergency fund?");
            pstmt.setString(2, "Money set aside for a vacation");
            pstmt.setString(3, "A fund for investing in stocks");
            pstmt.setString(4, "Savings for unexpected expenses");
            pstmt.setString(5, "A retirement account");
            pstmt.setString(6, "D");
            pstmt.setString(7, "An emergency fund is money saved to cover unexpected expenses like medical emergencies, car repairs, or job loss.");
            pstmt.setInt(8, 1);
            pstmt.executeUpdate();
            
            // Sample Question 5
            pstmt.setString(1, "What is the difference between a credit score and a credit report?");
            pstmt.setString(2, "They are the same thing");
            pstmt.setString(3, "A credit report is a number, while a credit score is a detailed history");
            pstmt.setString(4, "A credit score is a number based on your credit report");
            pstmt.setString(5, "Credit reports are only for loans, while credit scores are for credit cards");
            pstmt.setString(6, "C");
            pstmt.setString(7, "A credit score is a numerical value based on the information in your credit report, which is a detailed history of your credit usage.");
            pstmt.setInt(8, 3);
            pstmt.executeUpdate();
            
            System.out.println("Added sample quiz questions");
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }
    
    /**
     * Creates the user_goals table if it doesn't exist
     */
    private static void createUserGoalsTable(Connection conn) throws SQLException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS user_goals (" +
                    "id SERIAL PRIMARY KEY," +
                    "user_id INTEGER REFERENCES users(id)," +
                    "goal_type VARCHAR(20) NOT NULL," + // 'SAVING' or 'SPENDING'
                    "goal_name VARCHAR(100) NOT NULL," +
                    "target_amount DECIMAL(10,2) NOT NULL," +
                    "current_amount DECIMAL(10,2) DEFAULT 0.00," +
                    "start_date DATE NOT NULL DEFAULT CURRENT_DATE," +
                    "end_date DATE NOT NULL," +
                    "is_completed BOOLEAN DEFAULT FALSE," +
                    "reminder_frequency VARCHAR(20) DEFAULT 'WEEKLY'," + // 'DAILY', 'WEEKLY', 'MONTHLY'
                    "last_reminder_date DATE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(sql);
            System.out.println("Created or verified user_goals table");
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    /**
     * Creates a default user if no users exist
     * This ensures that the user_id foreign key constraint can be satisfied
     */
    private static void createDefaultUser(Connection conn) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            
            // Check if default user exists
            rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE id = 1");
            rs.next();
            if (rs.getInt(1) == 0) {
                // Create default user
                String sql = "INSERT INTO users (id, username, password, email) VALUES " +
                            "(1, 'default_user', 'password', 'default@example.com')";
                stmt.executeUpdate(sql);
                System.out.println("Created default user for testing");
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }
} 