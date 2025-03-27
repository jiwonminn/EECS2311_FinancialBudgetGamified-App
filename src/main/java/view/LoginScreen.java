package view;

import controller.UserControllerWithDatabase;
import utils.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginScreen extends BaseScreen {
    private JTextField emailField;
    private JPasswordField passwordField;
    private boolean isSubmitted = false;
    private String userEmail;
    private String userName;

    public LoginScreen() {
        super("Financial Budget Gamified - Login");
        JPanel mainPanel = createMainPanel();
        setContentPane(createWrapperPanel(mainPanel));
        initResizeListener();
        setVisible(true);
        fadeIn(0.5f);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(PANEL_COLOR);
        int horizontalPadding = (int) (getWidth() * 0.02);
        int verticalPadding = (int) (getHeight() * 0.03);
        mainPanel.setBorder(new EmptyBorder(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding));

        // Exit button panel
        mainPanel.add(createExitButtonPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Icon panel
        mainPanel.add(createIconPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Welcome & subtitle labels
        JLabel welcomeLabel = new JLabel("Welcome Adventurer");
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel subtitleLabel = new JLabel("Begin your financial quest");
        subtitleLabel.setForeground(new Color(180, 180, 180));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 35)));

        // Email field panel
        emailField = createTextField();
        emailField.setToolTipText("Enter a valid email (e.g., user@example.com)");
        JPanel emailPanel = createLabeledField("Email", emailField);
        mainPanel.add(emailPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Password field panel
        passwordField = createPasswordField();
        passwordField.setToolTipText("Password must be at least 3 characters");
        JPanel passwordPanel = createLabeledField("Password", passwordField);
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Login button
        JButton loginButton = createGradientButton("Enter the Realm", 45);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(loginButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Account info label
        JLabel accountLabel = new JLabel("Already have an account? Resume your journey");
        accountLabel.setForeground(new Color(180, 180, 180));
        accountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        accountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        accountPanel.setBackground(PANEL_COLOR);
        accountPanel.add(accountLabel);
        mainPanel.add(accountPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Register switch button
        JPanel registerSwitchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerSwitchPanel.setBackground(PANEL_COLOR);
        JButton registerSwitchButton = new JButton("Register");
        registerSwitchButton.setForeground(TEXT_COLOR);
        registerSwitchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerSwitchButton.setBorderPainted(false);
        registerSwitchButton.setFocusPainted(false);
        registerSwitchButton.setContentAreaFilled(false);
        registerSwitchButton.addActionListener(e ->
                fadeOutAndSwitch(() -> {
                    RegisterScreen regScreen = new RegisterScreen();
                    regScreen.fadeIn(0.5f);
                })
        );
        registerSwitchPanel.add(registerSwitchButton);
        mainPanel.add(registerSwitchPanel);

        Dimension panelSize = new Dimension(fieldWidth + 100, 600);
        mainPanel.setMinimumSize(panelSize);
        mainPanel.setPreferredSize(panelSize);

        // Login button action
        loginButton.addActionListener(e -> handleLoginAction());

        return mainPanel;
    }

    private void initResizeListener() {
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                fieldWidth = Math.max(300, (int) (getWidth() * 0.3));
                emailField.setMaximumSize(new Dimension(fieldWidth, 40));
                emailField.setPreferredSize(new Dimension(fieldWidth, 40));
                passwordField.setMaximumSize(new Dimension(fieldWidth, 40));
                passwordField.setPreferredSize(new Dimension(fieldWidth, 40));
                revalidate();
                repaint();
            }
        });
    }

    private void handleLoginAction() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter an email",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a password",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (password.length() < 3) {
            JOptionPane.showMessageDialog(
                    this,
                    "Password must be at least 3 characters long",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        UserControllerWithDatabase userController = new UserControllerWithDatabase();
        int userId = 0;
        try {
            userId = userController.authenticateUser(email, password);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        if (userId == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid email or password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Successful login
        userName = email.split("@")[0];
        userEmail = email;
        SessionManager.getInstance().setCurrentUser(userId, userName, userEmail);
        isSubmitted = true;
        dispose();

        final int finalUserId = userId;
        SwingUtilities.invokeLater(() -> {
            try {
                new CalendarUI(finalUserId, userName, userEmail);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

}
