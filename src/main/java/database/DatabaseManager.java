package database;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/ProjectTest";
    private static final String USER = "root";
    private static final String PASSWORD = "df118";
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to PostgreSQL database!");
            } catch (ClassNotFoundException e) {
                System.out.println("PostgreSQL JDBC Driver not found!");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Database connection failed!");
                e.printStackTrace();
            }
        }
        return connection;
    }
}