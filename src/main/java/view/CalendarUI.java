package main.java.view;

<<<<<<< Updated upstream
import main.java.controller.TransactionController;
import main.java.model.*;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.UtilDateModel;
=======
import controller.TransactionController;
import controller.UserController;
import model.*;
>>>>>>> Stashed changes

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarUI extends JFrame {
    private TransactionController transactionController;
    private UserController userController;
    private boolean isDatabaseConnected = false;
    private List<Transaction> localTransactions = new ArrayList<>();
    
    // UI components
    private JComboBox<Integer> dayComboBox;
    private JComboBox<Month> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private JPanel datePanel;
    private JTextField descriptionField;
    private JTextField amountField;
    private JRadioButton incomeButton;
    private JRadioButton expenseButton;
    private JTextArea transactionDisplay;
    private JComboBox<String> categoryComboBox;
    private final Color DARK_PURPLE = new Color(48, 16, 78);
    private final Color LIGHT_PURPLE = new Color(88, 24, 169);
    private final Color ACCENT_COLOR = new Color(255, 165, 0);

    public CalendarUI() {
<<<<<<< Updated upstream
        transactionController = new TransactionController();
        setTitle("Financial Budget Gamified - Transaction Logger");
        setSize(500, 400);
=======
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
        userController = new UserController(userName, userEmail, 1000);

        // Try to initialize the transaction controller (database connection)
        try {
            transactionController = new TransactionController();
            isDatabaseConnected = true;
        } catch (Exception e) {
            isDatabaseConnected = false;
            JOptionPane.showMessageDialog(this,
                "Could not connect to the database: " + e.getMessage() + 
                "\nThe application will run in offline mode, and transactions won't be saved permanently.",
                "Database Connection Error",
                JOptionPane.WARNING_MESSAGE);
        }

        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Financial Budget Gamified");
        setSize(600, 700);
>>>>>>> Stashed changes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Main panel with dark purple background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_PURPLE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create the input panel for transaction details
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(DARK_PURPLE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title label
        JLabel titleLabel = new JLabel("Log Your Quest Rewards");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(titleLabel);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Track your income and expenses to gain experience");
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(subtitleLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Transaction type selection
        JLabel typeLabel = new JLabel("Transaction Type");
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(typeLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        radioPanel.setBackground(DARK_PURPLE);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        expenseButton = new JRadioButton("Expense");
        expenseButton.setForeground(new Color(255, 100, 100));
        expenseButton.setBackground(DARK_PURPLE);
        expenseButton.setFont(new Font("Arial", Font.BOLD, 14));
        expenseButton.setSelected(true);
        expenseButton.setIcon(createColorCircle(new Color(255, 100, 100), 16));
        
        incomeButton = new JRadioButton("Income");
        incomeButton.setForeground(new Color(100, 255, 100));
        incomeButton.setBackground(DARK_PURPLE);
        incomeButton.setFont(new Font("Arial", Font.BOLD, 14));
        incomeButton.setIcon(createColorCircle(new Color(100, 255, 100), 16));
        
        ButtonGroup group = new ButtonGroup();
        group.add(expenseButton);
        group.add(incomeButton);
        
        radioPanel.add(expenseButton);
        radioPanel.add(incomeButton);
        inputPanel.add(radioPanel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Amount field
        JLabel amountLabel = new JLabel("Amount");
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(amountLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        amountField = new JTextField("Enter amount");
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        amountField.setBackground(new Color(30, 10, 60));
        amountField.setForeground(Color.WHITE);
        amountField.setCaretColor(Color.WHITE);
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_PURPLE),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        amountField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add focus listener to clear default text
        amountField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (amountField.getText().equals("Enter amount")) {
                    amountField.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (amountField.getText().isEmpty()) {
                    amountField.setText("Enter amount");
                }
            }
        });
        
        inputPanel.add(amountField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Category dropdown
        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setForeground(Color.WHITE);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(categoryLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Define categories with emojis
        String[] categories = {
            "🍴 Food & Dining",
            "🛍️ Shopping",
            "⚡ Utilities",
            "🏠 Housing",
            "🎮 Entertainment",
            "☕ Coffee",
            "🎁 Gifts",
            "🐖 Emergency Fund",
            "💳 Credit Card Debt",
            "📈 Stock Portfolio",
            "🏡 Home Down Payment",
            "🚗 Transportation",
            "💊 Healthcare",
            "📚 Education",
            "💼 Business"
        };
        
        categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryComboBox.setBackground(new Color(30, 10, 60));
        categoryComboBox.setForeground(Color.WHITE);
        categoryComboBox.setBorder(BorderFactory.createLineBorder(LIGHT_PURPLE));
        categoryComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new Color(100, 60, 140));
                } else {
                    c.setBackground(new Color(30, 10, 60));
                }
                c.setForeground(Color.WHITE);
                return c;
            }
        });
        
        categoryComboBox.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        categoryComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(categoryComboBox);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Description field
        JLabel descLabel = new JLabel("Description");
        descLabel.setForeground(Color.WHITE);
        descLabel.setFont(new Font("Arial", Font.BOLD, 16));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(descLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        descriptionField = new JTextField("Enter description");
        descriptionField.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionField.setBackground(new Color(30, 10, 60));
        descriptionField.setForeground(Color.WHITE);
        descriptionField.setCaretColor(Color.WHITE);
        descriptionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_PURPLE),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        descriptionField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        descriptionField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add focus listener to clear default text
        descriptionField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (descriptionField.getText().equals("Enter description")) {
                    descriptionField.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (descriptionField.getText().isEmpty()) {
                    descriptionField.setText("Enter description");
                }
            }
        });
        
        inputPanel.add(descriptionField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Log Transaction button
        JButton submitButton = new JButton("Log Transaction (+XP)");
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.setBackground(new Color(120, 80, 200));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        
        // Add hover effect
        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(140, 100, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(120, 80, 200));
            }
        });
        
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logTransaction();
            }
        });
        
        inputPanel.add(submitButton);
        
        // Add to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        
        // Transaction history panel
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(DARK_PURPLE);
        historyPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        transactionDisplay = new JTextArea(8, 40);
        transactionDisplay.setEditable(false);
        transactionDisplay.setBackground(new Color(30, 10, 60));
        transactionDisplay.setForeground(Color.WHITE);
        transactionDisplay.setFont(new Font("Monospaced", Font.PLAIN, 12));
        transactionDisplay.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(transactionDisplay);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_PURPLE));
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(historyPanel, BorderLayout.CENTER);
        
        // Add connection retry button if needed
        if (!isDatabaseConnected) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(DARK_PURPLE);
            JButton retryButton = new JButton("Retry Database Connection");
            retryButton.setBackground(new Color(80, 40, 120));
            retryButton.setForeground(Color.WHITE);
            retryButton.setFocusPainted(false);
            retryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    retryDatabaseConnection();
                }
            });
            buttonPanel.add(retryButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        }
        
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);  // Center the window
        setVisible(true);
        
        // Initialize transaction display
        updateTransactionDisplay();
    }
    
    private Icon createColorCircle(Color color, int size) {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(0, 0, size, size);
        g2.dispose();
        return new ImageIcon(image);
    }
    
    private void updateStatusLabel() {
        String status;
        Color color;
        
        if (isDatabaseConnected) {
            status = "Connected to database";
            color = new Color(100, 255, 100);
        } else {
            status = "Offline mode (database disconnected)";
            color = new Color(255, 100, 100);
        }
        
        transactionDisplay.append("Status: " + status + "\n\n");
    }
    
    private void retryDatabaseConnection() {
        try {
            transactionController = new TransactionController();
            isDatabaseConnected = true;
            updateTransactionDisplay();
            JOptionPane.showMessageDialog(this, 
                "Successfully connected to the database!",
                "Connection Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // Sync local transactions to database
            if (!localTransactions.isEmpty()) {
                int option = JOptionPane.showConfirmDialog(this,
                    "Would you like to save your " + localTransactions.size() + 
                    " offline transactions to the database?",
                    "Sync Transactions",
                    JOptionPane.YES_NO_OPTION);
                
                if (option == JOptionPane.YES_OPTION) {
                    for (Transaction t : localTransactions) {
                        transactionController.addTransaction(
                            t.getDescription(), 
                            t.getAmount(), 
                            t.getDate(), 
                            t.isIncome(),
                            t.getCategory());
                    }
                    localTransactions.clear();
                    updateTransactionDisplay();
                    JOptionPane.showMessageDialog(this,
                        "Transactions successfully synced to database!",
                        "Sync Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to connect to the database: " + e.getMessage(),
                "Connection Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logTransaction() {
        try {
            // Validate inputs
            if (descriptionField.getText().trim().isEmpty() || 
                descriptionField.getText().equals("Enter description")) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a description for the transaction", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount;
            try {
                String amountText = amountField.getText().trim();
                if (amountText.isEmpty() || amountText.equals("Enter amount")) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter an amount", 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter a positive amount", 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid number for amount", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate date = LocalDate.now();
            String description = descriptionField.getText().trim();
            String category = (String) categoryComboBox.getSelectedItem();
            boolean isIncome = incomeButton.isSelected();
            
            // Add the transaction (either to database or locally)
            if (isDatabaseConnected) {
                try {
                    transactionController.addTransaction(description, amount, date, isIncome, category);
                } catch (Exception e) {
                    // Database connection was lost
                    isDatabaseConnected = false;
                    
                    int option = JOptionPane.showConfirmDialog(this,
                        "Database connection lost. Would you like to save this transaction locally?",
                        "Connection Lost",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (option != JOptionPane.YES_OPTION) {
                        return;
                    }
                    
                    // Create a local transaction with category
                    Transaction transaction = new Transaction(description, amount, date, isIncome, category);
                    localTransactions.add(transaction);
                    JOptionPane.showMessageDialog(this,
                        "Transaction saved locally. You can sync to database later when connection is restored.",
                        "Local Save",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // Create a local transaction with category
                Transaction transaction = new Transaction(description, amount, date, isIncome, category);
                localTransactions.add(transaction);
            }
            
            // Transaction added successfully
            updateTransactionDisplay();
                
            // Clear input fields for next entry
            descriptionField.setText("Enter description");
            amountField.setText("Enter amount");
                
            // Show XP gained
            int xpGained = (int)(amount * 10);
            JOptionPane.showMessageDialog(this, 
                "Transaction recorded successfully!\n" +
                "You gained " + xpGained + " XP!",
                "Quest Completed", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTransactionDisplay() {
        transactionDisplay.setText("");
        
        // Show status
        updateStatusLabel();
        
        // Display database transactions
        if (isDatabaseConnected) {
            try {
                for (Transaction t : transactionController.getTransactions()) {
                    transactionDisplay.append(formatTransaction(t) + "\n");
                }
            } catch (Exception e) {
                // Database connection lost
                isDatabaseConnected = false;
                transactionDisplay.append("*** DATABASE CONNECTION LOST ***\n\n");
            }
        }
        
        // Display local transactions
        if (!localTransactions.isEmpty()) {
            if (isDatabaseConnected) {
                transactionDisplay.append("\n--- LOCAL TRANSACTIONS (NOT IN DATABASE) ---\n");
            }
            
            for (Transaction t : localTransactions) {
                transactionDisplay.append(formatTransaction(t) + "\n");
            }
        }
        
        // Show empty message if no transactions
        if ((isDatabaseConnected && transactionController.getTransactions().isEmpty()) 
                && localTransactions.isEmpty()) {
            transactionDisplay.append("No transactions recorded yet. Start your financial quest!");
        }
    }
    
    private String formatTransaction(Transaction t) {
        String type = t.isIncome() ? "INCOME" : "EXPENSE";
        String typeSymbol = t.isIncome() ? "+" : "-";
        return String.format("[%s] %s $%s%.2f - %s (%s)", 
                t.getDate(), type, typeSymbol, t.getAmount(), t.getDescription(), t.getCategory());
    }

    public static void main(String[] args) {
        try {
            // Use system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CalendarUI();
            }
        });
    }
}
