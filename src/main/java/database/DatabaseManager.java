package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/financial_budget_gamified";
    private static final String USER = "root";  // Change this to your MySQL username
    private static final String PASSWORD = "EECS2311Project";  // Change this to your MySQL password
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                //  Load MySQL JDBC Driver explicitly
                Class.forName("com.mysql.cj.jdbc.Driver");

                //  Connect to MySQL
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to MySQL database!");

            } catch (ClassNotFoundException e) {
                System.out.println("MySQL JDBC Driver not found!");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Database connection failed!");
                e.printStackTrace();
            }
        }
        return connection;
    }
}