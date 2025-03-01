package main.java.view;

import main.java.controller.TransactionController;
import main.java.controller.UserController;
import main.java.model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardPanel extends JPanel {
    
    private UserController userController;
    private TransactionController transactionController;
    private boolean isDatabaseConnected = false;
    
    // UI Components
    private JProgressBar levelProgressBar;
    private JLabel levelLabel;
    private JLabel xpLabel;
    private JPanel recentTransactionsPanel;
    
    public DashboardPanel(UserController userController) {
        this.userController = userController;
        
        // Try connecting to database
        try {
            transactionController = new TransactionController();
            isDatabaseConnected = true;
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
            isDatabaseConnected = false;
        }
        
        initializeUI();
    }
    
    private void initializeUI() {
        setBackground(MainUI.DARK_PURPLE);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Top panel - user level and progress
        JPanel userInfoPanel = createUserInfoPanel();
        add(userInfoPanel, BorderLayout.NORTH);
        
        // Center panel with transaction input and history
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);
        
        // Left panel - Log transactions
        JPanel logPanel = createLogTransactionPanel();
        
        // Right panel - Recent transactions
        JPanel historyPanel = createTransactionHistoryPanel();
        
        centerPanel.add(logPanel);
        centerPanel.add(historyPanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Update the transaction history data
        updateTransactionHistory();
    }
    
    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainUI.MEDIUM_PURPLE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // User level info
        levelLabel = new JLabel("Level " + userController.getUserLevel() + " Budget Warrior");
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(levelLabel, BorderLayout.WEST);
        
        // Level progress panel
        JPanel progressPanel = new JPanel(new BorderLayout(10, 0));
        progressPanel.setOpaque(false);
        
        // Level progress
        levelProgressBar = new JProgressBar(0, 1000);
        levelProgressBar.setValue(userController.getUserXP());
        levelProgressBar.setStringPainted(false);
        levelProgressBar.setForeground(new Color(120, 80, 200));
        levelProgressBar.setBackground(new Color(50, 30, 80));
        
        // XP Label
        xpLabel = new JLabel(userController.getUserXP() + " / 1000 XP");
        xpLabel.setForeground(Color.WHITE);
        xpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        progressPanel.add(levelProgressBar, BorderLayout.CENTER);
        progressPanel.add(xpLabel, BorderLayout.EAST);
        
        panel.add(progressPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLogTransactionPanel() {
        // This replaces our older CalendarUI
        TransactionLogPanel logPanel = new TransactionLogPanel(userController, transactionController, isDatabaseConnected);
        logPanel.addTransactionListener(new TransactionLogPanel.TransactionListener() {
            @Override
            public void onTransactionAdded(Transaction transaction) {
                // Update XP and level
                int xpGained = calculateXP(transaction);
                userController.addXP(xpGained);
                
                // Update UI components
                levelProgressBar.setValue(userController.getUserXP());
                xpLabel.setText(userController.getUserXP() + " / 1000 XP");
                levelLabel.setText("Level " + userController.getUserLevel() + " Budget Warrior");
                
                // Update transaction history
                updateTransactionHistory();
            }
        });
        
        return logPanel;
    }
    
    private JPanel createTransactionHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainUI.MEDIUM_PURPLE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Transaction history header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Recent Transactions");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel subtitleLabel = new JLabel("Your latest financial activities");
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Recent transactions list
        recentTransactionsPanel = new JPanel();
        recentTransactionsPanel.setLayout(new BoxLayout(recentTransactionsPanel, BoxLayout.Y_AXIS));
        recentTransactionsPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(recentTransactionsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateTransactionHistory() {
        recentTransactionsPanel.removeAll();
        
        if (isDatabaseConnected) {
            List<Transaction> transactions = transactionController.getTransactions();
            
            if (transactions.isEmpty()) {
                JLabel emptyLabel = new JLabel("No transactions yet. Start your financial journey!");
                emptyLabel.setForeground(Color.WHITE);
                emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                recentTransactionsPanel.add(emptyLabel);
            } else {
                // Show only the 5 most recent transactions
                int count = Math.min(transactions.size(), 5);
                for (int i = 0; i < count; i++) {
                    recentTransactionsPanel.add(createTransactionItem(transactions.get(i)));
                    
                    if (i < count - 1) {
                        recentTransactionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                    }
                }
            }
        } else {
            JLabel offlineLabel = new JLabel("Offline mode. Connect to database to view transactions.");
            offlineLabel.setForeground(new Color(255, 100, 100));
            offlineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            recentTransactionsPanel.add(offlineLabel);
        }
        
        recentTransactionsPanel.revalidate();
        recentTransactionsPanel.repaint();
    }
    
    private JPanel createTransactionItem(Transaction transaction) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 25, 65));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Left side with category icon and description
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        
        // Category icon would go here
        
        JPanel descPanel = new JPanel(new GridLayout(2, 1));
        descPanel.setOpaque(false);
        
        JLabel descLabel = new JLabel(transaction.getDescription());
        descLabel.setForeground(Color.WHITE);
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Date with category
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String dateText = transaction.getDate().format(formatter) + " • " + transaction.getCategory();
        
        JLabel dateLabel = new JLabel(dateText);
        dateLabel.setForeground(new Color(180, 180, 180));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        descPanel.add(descLabel);
        descPanel.add(dateLabel);
        
        leftPanel.add(descPanel, BorderLayout.CENTER);
        
        // Right side with amount
        JLabel amountLabel = new JLabel(formatAmount(transaction));
        amountLabel.setForeground(transaction.isIncome() ? new Color(100, 255, 100) : new Color(255, 100, 100));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(amountLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private String formatAmount(Transaction transaction) {
        return (transaction.isIncome() ? "+" : "-") + 
               String.format("$%.2f", transaction.getAmount());
    }
    
    private int calculateXP(Transaction transaction) {
        // Simple XP calculation based on amount
        return (int)(transaction.getAmount() * 10);
    }
} 