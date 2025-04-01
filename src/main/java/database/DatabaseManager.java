package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseManager{
    // Updated URL to match host port (5431) and database name (KKS)
    private static final String URL = "jdbc:postgresql://localhost:5431/KKS";
    private static final String USER = "khalifa";
    private static final String PASSWORD = "your_password";

    // Always return a new connection
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found!", e);
        }
        
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        
        // Ensure the username column exists in the users table
        ensureUsernameColumnExists(connection);
        
        return connection;
    }

    private static void ensureUsernameColumnExists(Connection connection) {
        try {
            // PostgreSQL-specific way to add a column if it doesn't exist
            Statement stmt = connection.createStatement();
            stmt.execute(
                "DO $$ BEGIN " +
                "    BEGIN " +
                "        ALTER TABLE users ADD COLUMN username VARCHAR(100); " +
                "    EXCEPTION " +
                "        WHEN duplicate_column THEN NULL; " +
                "    END; " +
                "END $$;"
            );
            stmt.close();
            System.out.println("Username column checked/added to users table");
        } catch (Exception e) {
            System.err.println("Error ensuring username column exists: " + e.getMessage());
            // Continue without failing - the application will use email fallback
        }
    }
}
