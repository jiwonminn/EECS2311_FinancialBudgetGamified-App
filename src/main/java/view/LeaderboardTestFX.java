package view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Test class to demonstrate the JavaFX Leaderboard.
 * This class creates a simple JFrame and adds the JavaFX leaderboard component to it.
 */
public class LeaderboardTestFX {

    /**
     * Main method to test the JavaFX leaderboard implementation.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Launch the UI on the EDT
        SwingUtilities.invokeLater(() -> {
            // Create and setup the frame
            JFrame frame = new JFrame("Leaderboard JavaFX Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            // Create an instance of the JavaFX leaderboard
            // Use a placeholder user ID (1) for testing
            LeaderboardFX leaderboard = new LeaderboardFX(1);
            
            // Add the leaderboard to the frame
            frame.add(leaderboard, BorderLayout.CENTER);
            
            // Set frame size and make visible
            frame.setPreferredSize(new Dimension(800, 700));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
} 