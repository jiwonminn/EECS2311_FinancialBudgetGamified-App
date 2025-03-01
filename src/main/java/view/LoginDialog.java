package main.java.view;

import main.java.controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    
    private JTextField nameField;
    private JTextField emailField;
    private boolean isLoginSuccessful = false;
    private UserController userController;
    
    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(MainUI.DARK_PURPLE);
        
        // Title
        JLabel titleLabel = new JLabel("Financial Budget Quest");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Begin your financial journey");
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Name field
        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        nameField = new JTextField(20);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        styleTextField(nameField);
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Email field
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(emailLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        styleTextField(emailField);
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Initial balance (this could be moved to a registration flow for a real app)
        JLabel balanceLabel = new JLabel("Starting Balance:");
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(balanceLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JTextField balanceField = new JTextField("1000.00");
        balanceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        styleTextField(balanceField);
        panel.add(balanceField);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(120, 120, 120));
        
        JButton loginButton = new JButton("Start Adventure");
        styleButton(loginButton, MainUI.LIGHT_PURPLE);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(loginButton);
        
        panel.add(buttonPanel);
        
        // Action listeners
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Validate inputs
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    double initialBalance = Double.parseDouble(balanceField.getText().trim());
                    
                    if (name.isEmpty() || email.isEmpty()) {
                        JOptionPane.showMessageDialog(LoginDialog.this,
                                "Please enter your name and email to continue.",
                                "Missing Information", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Create user controller
                    userController = new UserController(name, email, initialBalance);
                    isLoginSuccessful = true;
                    dispose();
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LoginDialog.this,
                            "Please enter a valid starting balance.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Set dialog properties
        getContentPane().add(panel);
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBackground(new Color(48, 30, 75));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainUI.LIGHT_PURPLE),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
    }
    
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
    }
    
    public boolean isLoginSuccessful() {
        return isLoginSuccessful;
    }
    
    public UserController getUserController() {
        return userController;
    }
} 