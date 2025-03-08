package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
<<<<<<< HEAD
    private static final String URL = "jdbc:postgresql://localhost:5432/demodb"; // change if needed
    private static final String USER = "postgres";
    private static final String PASSWORD = "joshuang0827";
=======
    private static final String URL = "jdbc:mysql://localhost:3306/ProjectTest";
    private static final String USER = "root";
    private static final String PASSWORD = "df118";
    private static Connection connection = null;
>>>>>>> refs/remotes/origin/Peyton

<<<<<<< HEAD
    // Always return a new connection
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found!", e);
=======
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to mySQL database!");
            } catch (ClassNotFoundException e) {
                System.out.println("mySQL JDBC Driver not found!");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Database connection failed!");
                e.printStackTrace();
            }
>>>>>>> refs/remotes/origin/Peyton
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
