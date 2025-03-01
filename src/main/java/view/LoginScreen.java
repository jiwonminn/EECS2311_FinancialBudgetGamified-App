package view;

import controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

public class LoginScreen extends JFrame {
    // Define colors
    private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    private final Color PANEL_COLOR = new Color(40, 24, 69);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color ACCENT_COLOR = new Color(128, 90, 213);
    private final Color FIELD_BACKGROUND = new Color(50, 35, 80);
    private final Color FIELD_BORDER = new Color(70, 50, 110);
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private boolean isSubmitted = false;
    private String userEmail;
    private String userName;
    
    public LoginScreen() {
        setTitle("Financial Budget Gamified - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set larger size and make responsive
        setSize(1920, 1080);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        // Create a wrapper panel with GridBagLayout for centering
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(BACKGROUND_COLOR);
        
        // Main content panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(PANEL_COLOR);
        
        // Calculate responsive padding - scales with window size
        int horizontalPadding = (int)(getWidth() * 0.02);
        int verticalPadding = (int)(getHeight() * 0.03);
        mainPanel.setBorder(new EmptyBorder(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding));
        
        // Logo/Icon panel
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconPanel.setBackground(PANEL_COLOR);
        JLabel iconLabel = createShieldIcon();
        iconPanel.add(iconLabel);
        
        // Welcome text
        JLabel welcomeLabel = new JLabel("Welcome Adventurer");
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Begin your financial quest");
        subtitleLabel.setForeground(new Color(180, 180, 180));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Input field panels with proper centering
        JPanel inputsPanel = new JPanel();
        inputsPanel.setLayout(new BoxLayout(inputsPanel, BoxLayout.Y_AXIS));
        inputsPanel.setBackground(PANEL_COLOR);
        inputsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Calculate responsive field width - 30% of window width but minimum 300px
        int fieldWidth = Math.max(300, (int)(getWidth() * 0.3));
        
        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setForeground(TEXT_COLOR);
        
        emailField = new JTextField();
        emailField.setBackground(FIELD_BACKGROUND);
        emailField.setForeground(TEXT_COLOR);
        emailField.setCaretColor(TEXT_COLOR);
        emailField.setHorizontalAlignment(JTextField.CENTER); // Center text
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        emailField.setMaximumSize(new Dimension(fieldWidth, 40));
        emailField.setPreferredSize(new Dimension(fieldWidth, 40));
        
        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(TEXT_COLOR);
        
        passwordField = new JPasswordField();
        passwordField.setBackground(FIELD_BACKGROUND);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setCaretColor(TEXT_COLOR);
        passwordField.setHorizontalAlignment(JTextField.CENTER); // Center text
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setMaximumSize(new Dimension(fieldWidth, 40));
        passwordField.setPreferredSize(new Dimension(fieldWidth, 40));
        
        // Add centered input fields
        JPanel emailLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        emailLabelPanel.setBackground(PANEL_COLOR);
        emailLabelPanel.add(emailLabel);
        
        JPanel emailFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        emailFieldPanel.setBackground(PANEL_COLOR);
        emailFieldPanel.add(emailField);
        
        JPanel passwordLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordLabelPanel.setBackground(PANEL_COLOR);
        passwordLabelPanel.add(passwordLabel);
        
        JPanel passwordFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordFieldPanel.setBackground(PANEL_COLOR);
        passwordFieldPanel.add(passwordField);
        
        // Login button with gradient
        JButton loginButton = new JButton("Enter the Realm") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient from purple to blue
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(128, 90, 213),
                    getWidth(), getHeight(), new Color(90, 140, 255)
                );
                
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.setColor(TEXT_COLOR);
                String text = getText();
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(fieldWidth, 45);
            }
        };
        
        loginButton.setForeground(TEXT_COLOR);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(loginButton);
        
        // Already have account text
        JLabel accountLabel = new JLabel("Already have an account? Resume your journey");
        accountLabel.setForeground(new Color(180, 180, 180));
        accountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        accountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        accountPanel.setBackground(PANEL_COLOR);
        accountPanel.add(accountLabel);
        
        // Add components to main panel with spacing
        mainPanel.add(iconPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        mainPanel.add(emailLabelPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(emailFieldPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(passwordLabelPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(passwordFieldPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(accountPanel);
        
        // Make the login panel responsive
        Dimension loginPanelSize = new Dimension(fieldWidth + 100, 600);
        mainPanel.setMinimumSize(loginPanelSize);
        mainPanel.setPreferredSize(loginPanelSize);
        
        // Add the main panel to the centered wrapper
        wrapperPanel.add(mainPanel);
        
        // Add action listener to login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                
                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        LoginScreen.this,
                        "Please enter both email and password",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                
                // For now, just extract username from email
                userName = email.split("@")[0];
                userEmail = email;
                isSubmitted = true;
                
                dispose(); // Close the login screen
                
                // Start the main application
                SwingUtilities.invokeLater(() -> new CalendarUI(userName, userEmail));
            }
        });
        
        // Add window resize listener to adjust component sizes
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int newFieldWidth = Math.max(300, (int)(getWidth() * 0.3));
                emailField.setMaximumSize(new Dimension(newFieldWidth, 40));
                emailField.setPreferredSize(new Dimension(newFieldWidth, 40));
                passwordField.setMaximumSize(new Dimension(newFieldWidth, 40));
                passwordField.setPreferredSize(new Dimension(newFieldWidth, 40));
                loginButton.setPreferredSize(new Dimension(newFieldWidth, 45));
                
                // Force UI to update
                revalidate();
                repaint();
            }
        });
        
        setContentPane(wrapperPanel);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setVisible(true);
    }
    
    private JLabel createShieldIcon() {
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create circular background
                Ellipse2D circle = new Ellipse2D.Double(0, 0, 70, 70);
                g2d.setColor(ACCENT_COLOR);
                g2d.fill(circle);
                
                // Draw shield icon
                g2d.setColor(TEXT_COLOR);
                int shieldWidth = 30;
                int shieldHeight = 40;
                int x = (70 - shieldWidth) / 2;
                int y = (70 - shieldHeight) / 2;
                
                // Shield outline
                g2d.setStroke(new BasicStroke(2f));
                g2d.fillRoundRect(x, y, shieldWidth, shieldHeight, 10, 10);
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(70, 70);
            }
        };
        
        return iconLabel;
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
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Add custom styling to UI defaults
            UIManager.put("OptionPane.background", new Color(40, 24, 69));
            UIManager.put("Panel.background", new Color(40, 24, 69));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
} 