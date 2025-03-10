package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
	private static final String URL = "jdbc:postgresql://localhost:5432/demodb"; // change if needed
    private static final String USER = "postgres";
    private static final String PASSWORD = "joshuang0827";


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
