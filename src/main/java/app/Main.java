package app;

import java.awt.Color;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
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
            // Initialize JavaFX toolkit properly
            Platform.setImplicitExit(false);
            // We don't need to create a JFXPanel instance here
            
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Add custom styling to UI defaults
            UIManager.put("OptionPane.background", new Color(40, 24, 69));
            UIManager.put("Panel.background", new Color(40, 24, 69));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        // Launch login screen
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
} 