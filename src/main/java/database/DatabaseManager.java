package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
