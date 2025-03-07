package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
<<<<<<< HEAD
    private static final String URL = "jdbc:mysql://localhost:3306/financial_budget_gamified";
    private static final String USER = "root";  // Change this to your MySQL username
    private static final String PASSWORD = "EECS2311Project";  // Change this to your MySQL password
    private static Connection connection = null;
=======
    private static final String URL = "jdbc:postgresql://localhost:5432/Projecttest"; // change if needed
    private static final String USER = "khalifa";
    private static final String PASSWORD = "";
>>>>>>> refs/heads/main

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