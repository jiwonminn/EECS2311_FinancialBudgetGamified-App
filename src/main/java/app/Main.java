package app;

import java.awt.Color;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import view.LoginScreen;
//import database.DatabaseUpdater;

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
    public static void main(String[] args) throws SQLException {

        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Add custom styling to UI defaults
            UIManager.put("OptionPane.background", new Color(40, 24, 69));
            UIManager.put("Panel.background", new Color(40, 24, 69));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Update database schema if needed
//        try {
//            // Ensure the database is up to date
//            DatabaseUpdater.updateTransactionsTable();
//            System.out.println("Database schema check completed.");
//        } catch (Exception e) {
//            System.err.println("Failed to update database schema: " + e.getMessage());
//            e.printStackTrace();
//
//            // Show error dialog to user
//            JOptionPane.showMessageDialog(null,
//                "There was a problem connecting to the database. Some features may not work properly.\n" +
//                "Error: " + e.getMessage(),
//                "Database Error",
//                JOptionPane.ERROR_MESSAGE);
//        }
        
        // Launch login screen
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
} 