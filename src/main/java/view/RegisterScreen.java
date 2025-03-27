package view;

import controller.UserControllerWithDatabase;
import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class RegisterScreen extends BaseScreen {
    private JTextField emailField;
    private JPasswordField passwordField;

    public RegisterScreen() {
        super("Financial Budget Gamified - Register");
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
        // Icon panel
        mainPanel.add(createIconPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Welcome & subtitle labels
        JLabel welcomeLabel = new JLabel("Register a New Account");
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel subtitleLabel = new JLabel("Begin your financial journey");
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

        // Register button
        JButton registerButton = createGradientButton("Create Account", 45);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login switch label
        JLabel loginLabel = new JLabel("Already have an account? Login here");
        loginLabel.setForeground(new Color(180, 180, 180));
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fadeOutAndSwitch(() -> new LoginScreen());
            }
        });
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginPanel.setBackground(PANEL_COLOR);
        loginPanel.add(loginLabel);
        mainPanel.add(loginPanel);

        Dimension panelSize = new Dimension(fieldWidth + 100, 600);
        mainPanel.setMinimumSize(panelSize);
        mainPanel.setPreferredSize(panelSize);

        // Register button action
        registerButton.addActionListener(e -> handleRegisterAction());

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

    private void handleRegisterAction() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both email and password",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!email.matches("^[\\w-.]+@[\\w-]+\\.[\\w]{2,}$")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid email format. For example, user@example.com",
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
            userId = userController.registerUser(email, password);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        if (userId != -1) {
            fadeOutAndSwitch(() -> {
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.fadeIn(0.5f);
            });
            User newUser = new User(userId, email.split("@")[0], email, 0.0, 0);
            // Optionally handle post-registration actions here
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Registration failed. Please try again.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
