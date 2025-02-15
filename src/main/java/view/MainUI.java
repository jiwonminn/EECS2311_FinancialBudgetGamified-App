package view;

import controller.UserController;
import javax.swing.*;

public class MainUI {
    public static void main(String[] args) {
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

        System.out.println("Welcome to Financial Budget Gamified App!");
        System.out.println(userController.getUserInfo());

        userController.updateBalance(-200);
        userController.addPoints(10);

        System.out.println("Updated User Info: " + userController.getUserInfo());

        // Launch the main dashboard (if applicable)
        SwingUtilities.invokeLater(() -> new DashboardGUI());
    }
}
