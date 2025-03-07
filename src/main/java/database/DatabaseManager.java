package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/demodb";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "joshuang0827";
    
    private static Connection connection = null;
    private static Properties props = null;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        props = new Properties();
        try {
            // Try to load from properties file first
            String configPath = "src/main/resources/database.properties";
            FileInputStream input = new FileInputStream(configPath);
            props.load(input);
            input.close();
            System.out.println("Loaded database properties from file");
        } catch (IOException e) {
            // If file not found, use default values
            System.out.println("No database.properties file found, using default values");
            props.setProperty("db.url", DEFAULT_URL);
            props.setProperty("db.user", DEFAULT_USER);
            props.setProperty("db.password", DEFAULT_PASSWORD);
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Ensure the PostgreSQL driver is loaded
                Class.forName("org.postgresql.Driver");
                
                // Get connection details from properties
                String url = props.getProperty("db.url", DEFAULT_URL);
                String user = props.getProperty("db.user", DEFAULT_USER);
                String password = props.getProperty("db.password", DEFAULT_PASSWORD);
                
                System.out.println("Connecting to: " + url + " with user: " + user);
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Successfully connected to PostgreSQL database!");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
        return null;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null; // Set to null so new connection can be created if needed
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
