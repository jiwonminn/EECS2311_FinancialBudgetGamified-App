package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Simple test class to verify database connectivity
 */
public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Get database connection
            conn = DatabaseManager.getConnection();
            
            if (conn != null) {
                System.out.println("Connection successful!");
                
                // Test executing a simple query
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT current_database(), current_user");
                
                if (rs.next()) {
                    System.out.println("Connected to database: " + rs.getString(1));
                    System.out.println("Connected as user: " + rs.getString(2));
                }
                
                rs.close();
            } else {
                System.err.println("Failed to establish connection!");
            }
            
        } catch (SQLException e) {
            System.err.println("Database test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close statement
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            // Close connection
            DatabaseManager.closeConnection();
        }
    }
} 