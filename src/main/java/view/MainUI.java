package main.java.view;

import main.java.controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUI extends JFrame {
    
    // UI Colors based on the theme
    public static final Color DARK_PURPLE = new Color(28, 19, 45);
    public static final Color MEDIUM_PURPLE = new Color(48, 30, 75);
    public static final Color LIGHT_PURPLE = new Color(88, 24, 169);
    public static final Color ACCENT_COLOR = new Color(255, 165, 0);
    
    // Main panels
    private JPanel contentPane;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Tab panels
    private DashboardPanel dashboardPanel;
    private TransactionPanel transactionPanel;
    private QuizPanel quizPanel;
    private LeaderboardPanel leaderboardPanel;
    
    // User info
    private UserController userController;
    
    public MainUI() {
        setupUserInfo();
        initializeUI();
    }
    
    private void setupUserInfo() {
        // Show login dialog first
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);
        
        if (loginDialog.isLoginSuccessful()) {
            userController = loginDialog.getUserController();
        } else {
            // Exit if login was cancelled
            System.exit(0);
        }
    }
    
    private void initializeUI() {
        setTitle("Financial Budget Gamified");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        
        // Setup main content pane
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(DARK_PURPLE);
        
        // Create navigation header
        JPanel navPanel = createNavigationPanel();
        contentPane.add(navPanel, BorderLayout.NORTH);
        
        // Create main panel with card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(DARK_PURPLE);
        
        // Initialize tab panels
        dashboardPanel = new DashboardPanel(userController);
        transactionPanel = new TransactionPanel(userController);
        quizPanel = new QuizPanel(userController);
        leaderboardPanel = new LeaderboardPanel();
        
        // Add panels to card layout
        mainPanel.add(dashboardPanel, "Dashboard");
        mainPanel.add(transactionPanel, "Transactions");
        mainPanel.add(quizPanel, "Quiz");
        mainPanel.add(leaderboardPanel, "Leaderboard");
        
        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        // Set content pane
        setContentPane(contentPane);
        
        // Start with dashboard
        cardLayout.show(mainPanel, "Dashboard");
        
        // Center window
        setLocationRelativeTo(null);
    }
    
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS));
        navPanel.setBackground(MEDIUM_PURPLE);
        navPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        // Create navigation buttons for each tab
        JButton dashboardBtn = createNavButton("Dashboard", "Dashboard");
        JButton transactionsBtn = createNavButton("Transactions", "Transactions");
        JButton quizBtn = createNavButton("Quiz", "Quiz");
        JButton leaderboardBtn = createNavButton("Leaderboard", "Leaderboard");
        
        // Create user info display
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Level " + userController.getUserLevel() + " Budget Warrior");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        userPanel.add(userLabel);
        
        // Add all elements to navigation panel
        navPanel.add(dashboardBtn);
        navPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        navPanel.add(transactionsBtn);
        navPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        navPanel.add(quizBtn);
        navPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        navPanel.add(leaderboardBtn);
        navPanel.add(Box.createHorizontalGlue());
        navPanel.add(userPanel);
        
        return navPanel;
    }
    
    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(MEDIUM_PURPLE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, cardName);
            }
        });
        
        return button;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set some UI defaults for the theme
            UIManager.put("Panel.background", DARK_PURPLE);
            UIManager.put("OptionPane.background", DARK_PURPLE);
            UIManager.put("Button.background", MEDIUM_PURPLE);
            UIManager.put("Button.foreground", Color.WHITE);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainUI().setVisible(true);
            }
        });
    }
}
