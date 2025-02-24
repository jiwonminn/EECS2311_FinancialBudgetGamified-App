package view;

import controller.UserController;
import controller.BudgetController;
import javax.swing.*;
import java.awt.*;

public class DashboardGUI extends JFrame {
    private UserController userController;
    private BudgetController budgetController;
    private JLabel balanceLabel;
    private JLabel pointsLabel;
    
    public DashboardGUI() {
        setTitle("Financial Budget Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Initialize controllers
        budgetController = new BudgetController(1000, 200);
        
        setupUI();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void setupUI() {
        // Top Panel - User Info
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - Main Content
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Side Panel - Navigation
        JPanel sidePanel = createSidePanel();
        add(sidePanel, BorderLayout.WEST);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(new Color(240, 240, 240));
        
        balanceLabel = new JLabel("Balance: $1000");
        pointsLabel = new JLabel("Points: 0");
        
        panel.add(balanceLabel);
        panel.add(pointsLabel);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Budget Overview
        JPanel budgetPanel = new JPanel();
        budgetPanel.setBorder(BorderFactory.createTitledBorder("Budget Overview"));
        panel.add(budgetPanel);
        
        // Recent Transactions
        JPanel transactionsPanel = new JPanel();
        transactionsPanel.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        panel.add(transactionsPanel);
        
        // Goals Progress
        JPanel goalsPanel = new JPanel();
        goalsPanel.setBorder(BorderFactory.createTitledBorder("Financial Goals"));
        panel.add(goalsPanel);
        
        // Achievements
        JPanel achievementsPanel = new JPanel();
        achievementsPanel.setBorder(BorderFactory.createTitledBorder("Achievements"));
        panel.add(achievementsPanel);
        
        return panel;
    }
    
    private JPanel createSidePanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(200, 0));
        
        JButton calendarButton = new JButton("Calendar");
        calendarButton.addActionListener(e -> new CalendarUI());
        
        JButton quizButton = new JButton("Take Quiz");
        quizButton.addActionListener(e -> new QuizUI(userController));
        
        JButton leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.addActionListener(e -> new LeaderboardUI());
        
        JButton settingsButton = new JButton("Settings");
        JButton helpButton = new JButton("Help");
        
        panel.add(calendarButton);
        panel.add(quizButton);
        panel.add(leaderboardButton);
        panel.add(settingsButton);
        panel.add(helpButton);
        
        return panel;
    }
    
    public void updateUserInfo(String balance, int points) {
        balanceLabel.setText("Balance: $" + balance);
        pointsLabel.setText("Points: " + points);
    }
}
