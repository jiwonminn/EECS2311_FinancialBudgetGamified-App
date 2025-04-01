package view;

import controller.UserProfileController;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class UserProfileDialog extends JDialog {
    // Define colors to match the application theme
    private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    private final Color PANEL_COLOR = new Color(40, 24, 69);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color ACCENT_COLOR = new Color(128, 90, 213);
    private final Color FIELD_BACKGROUND = new Color(50, 35, 80);
    private final Color FIELD_BORDER = new Color(70, 50, 110);
    
    private UserProfileController controller;
    private JLabel emailLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton updateButton;
    private JButton cancelButton;
    private String currentUsername; // To store original username for comparison

    public UserProfileDialog(Frame owner, int userId) throws SQLException {
        super(owner, "User Profile Settings", true);
        controller = new UserProfileController(userId);
        initializeUI();
        loadUserProfile();
        setSize(400, 320);
        setLocationRelativeTo(owner);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Email section
        JLabel emailTitle = new JLabel("Email:");
        emailTitle.setForeground(TEXT_COLOR);
        emailTitle.setFont(new Font("Dialog", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(emailTitle, gbc);

        emailLabel = new JLabel();
        emailLabel.setForeground(TEXT_COLOR);
        emailLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        gbc.gridx = 1;
        contentPanel.add(emailLabel, gbc);
        
        // Username section
        JLabel usernameTitle = new JLabel("Username:");
        usernameTitle.setForeground(TEXT_COLOR);
        usernameTitle.setFont(new Font("Dialog", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(usernameTitle, gbc);

        usernameField = new JTextField(20);
        usernameField.setBackground(FIELD_BACKGROUND);
        usernameField.setForeground(TEXT_COLOR);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        usernameField.setCaretColor(TEXT_COLOR);
        gbc.gridx = 1;
        contentPanel.add(usernameField, gbc);

        // Password section
        JLabel passwordTitle = new JLabel("Password:");
        passwordTitle.setForeground(TEXT_COLOR);
        passwordTitle.setFont(new Font("Dialog", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(passwordTitle, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setBackground(FIELD_BACKGROUND);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        passwordField.setCaretColor(TEXT_COLOR);
        gbc.gridx = 1;
        contentPanel.add(passwordField, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(PANEL_COLOR);
        cancelButton.setForeground(TEXT_COLOR);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        updateButton = new JButton("Update");
        updateButton.setBackground(ACCENT_COLOR);
        updateButton.setForeground(TEXT_COLOR);
        updateButton.setFocusPainted(false);
        updateButton.setBorderPainted(false);
        updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(updateButton);
        add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> updateProfile());
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadUserProfile() {
        try {
            User user = controller.getUserProfile();
            if (user != null) {
                emailLabel.setText(user.getEmail());
                
                // Get username with fallback to email prefix
                currentUsername = controller.getUsername();
                usernameField.setText(currentUsername);
                
                // Retrieve the password directly from the database
                String currentPassword = controller.getUserPassword();
                passwordField.setText(currentPassword);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProfile() {
        String newUsername = usernameField.getText().trim();
        String newPassword = new String(passwordField.getPassword());
        
        // Validation checks
        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Username cannot be empty", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.length() < 3) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 3 characters long",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable buttons to prevent multiple clicks
        updateButton.setEnabled(false);
        cancelButton.setEnabled(false);

        // Run the update in a background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                boolean passwordSuccess = controller.updatePassword(newPassword);
                
                // Only check for username availability if it has changed
                boolean usernameSuccess = true;
                if (!newUsername.equals(currentUsername)) {
                    // Check if the new username is available (not taken)
                    if (!controller.isUsernameAvailable(newUsername)) {
                        throw new SQLException("Username '" + newUsername + "' is already taken");
                    }
                    usernameSuccess = controller.updateUsername(newUsername);
                }
                
                return passwordSuccess && usernameSuccess;
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        // Close the dialog immediately upon successful update
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(
                                UserProfileDialog.this,
                                "Profile update failed.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        updateButton.setEnabled(true);
                        cancelButton.setEnabled(true);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            UserProfileDialog.this,
                            "Error updating profile: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    updateButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}