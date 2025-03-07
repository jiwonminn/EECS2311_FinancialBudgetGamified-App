package app;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import database.DatabaseManager;
import database.DatabaseUpdater;
import view.LoginScreen;

/**
 * Main entry point for the Financial Budget Gamified Application.
 * Centralizes the application startup into a single location.
 */
public class Main {
    
    /**
     * Main method that starts the application.
     * Sets up the UI look and feel, updates the database if needed,
     * and launches the login screen.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set the look and feel to the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
        }
        
        // Initialize the database schema
        try {
            System.out.println("Initializing database...");
            DatabaseUpdater.initializeDatabase();
            System.out.println("Database initialization complete.");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                    "Database initialization failed: " + e.getMessage() + "\nThe application may not work correctly.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        // Add a shutdown hook to close database connections properly
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Application shutting down. Closing database connections...");
            DatabaseManager.closeConnection();
        }));
        
        // Launch the login screen
        SwingUtilities.invokeLater(() -> {
            try {
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error launching application: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                        "Failed to start application: " + e.getMessage(), 
                        "Application Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
} 