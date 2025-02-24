package view;

import controller.UserController;
import javax.swing.*;

public class MainUI {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Show the user info pop-up before proceeding
            UserInfoDialog userInfoDialog = new UserInfoDialog(null);
            userInfoDialog.setVisible(true);

            // If the user didn't submit details, exit the program
            if (!userInfoDialog.isSubmitted()) {
                System.exit(0);
            }

            // Get user details
            String userName = userInfoDialog.getUserName();
            String userEmail = userInfoDialog.getUserEmail();

            // Initialize the UserController with user details
            UserController userController = new UserController(userName, userEmail, 1000);

            // Launch the main dashboard
            DashboardGUI dashboard = new DashboardGUI();
            dashboard.setVisible(true);
        });
    }

    private static void addGameElements(UserController userController) {
        JButton quizButton = new JButton("Take Financial Quiz");
        quizButton.addActionListener(e -> new QuizUI(userController));
        
        JButton leaderboardButton = new JButton("View Leaderboard");
        leaderboardButton.addActionListener(e -> new LeaderboardUI());
        
        // Add these buttons to your UI
    }
}
