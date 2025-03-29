package view;

import controller.UserProfileController;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class UserProfileDialog extends JDialog {
    private UserProfileController controller;
    private JLabel emailLabel;
    private JPasswordField passwordField;
    private JButton updateButton;
    private JButton cancelButton;

    public UserProfileDialog(Frame owner, int userId) throws SQLException {
        super(owner, "User Profile", true);
        controller = new UserProfileController(userId);
        initializeUI();
        loadUserProfile();
        setSize(400, 250);
        setLocationRelativeTo(owner);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(24, 15, 41));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel emailTitle = new JLabel("Email:");
        emailTitle.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(emailTitle, gbc);

        emailLabel = new JLabel();
        emailLabel.setForeground(Color.WHITE);
        gbc.gridx = 1;
        contentPanel.add(emailLabel, gbc);

        JLabel passwordTitle = new JLabel("Password:");
        passwordTitle.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(passwordTitle, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        contentPanel.add(passwordField, gbc);

        add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(24, 15, 41));
        updateButton = new JButton("Update");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> updatePassword());
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadUserProfile() {
        try {
            User user = controller.getUserProfile();
            if (user != null) {
                emailLabel.setText(user.getEmail());
                // Retrieve the password directly from the database
                String currentPassword = controller.getUserPassword();
                passwordField.setText(currentPassword);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePassword() {
        String newPassword = new String(passwordField.getPassword());
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to update your password?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (newPassword.length() < 3) {
                JOptionPane.showMessageDialog(
                        this,
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
                    return controller.updatePassword(newPassword);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            // Close the dialog immediately upon successful password update
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(
                                    UserProfileDialog.this,
                                    "Password update failed.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            updateButton.setEnabled(true);
                            cancelButton.setEnabled(true);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                UserProfileDialog.this,
                                "Error updating password: " + ex.getMessage(),
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
}
