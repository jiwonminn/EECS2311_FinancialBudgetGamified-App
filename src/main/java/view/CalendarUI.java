package view;

import controller.TransactionController;
import controller.UserController;
import controller.QuizController;
import model.*;
import view.GoalsUI;
import view.CustomCalendarPicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.List;
import javax.swing.SpinnerDateModel;

public class CalendarUI extends JFrame {
    // Define colors
    private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    private final Color PANEL_COLOR = new Color(40, 24, 69);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color ACCENT_COLOR = new Color(128, 90, 213);
    private final Color EXPENSE_COLOR = new Color(215, 38, 61);
    private final Color INCOME_COLOR = new Color(39, 174, 96);
    private final Color FIELD_BACKGROUND = new Color(50, 35, 80);
    private final Color FIELD_BORDER = new Color(70, 50, 110);

    private TransactionController transactionController;
    private CustomCalendarPicker datePicker;
    private JTextField descriptionField;
    private JTextField amountField;
    private JRadioButton incomeButton;
    private JRadioButton expenseButton;
    private JPanel transactionDisplayPanel;
    private JComboBox<String> categoryComboBox;
    private int userId;
    private String userName;
    private String userEmail;

    public CalendarUI(int userId, String userName, String userEmail) throws SQLException {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        
        // Initialize the UserController with user details
        UserController userController = new UserController(userName, userEmail, 1000);

        transactionController = new TransactionController();
        transactionController.setUserId(userId);
        setTitle("Financial Budget Gamified - Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Use a BorderLayout for the main frame
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Initialize the UI with the Dashboard tab
        switchTab("Dashboard");
        
        // Center the window on screen
        setLocationRelativeTo(null);
        
        setVisible(true);
    }
    
    // Add default constructor for backward compatibility
    public CalendarUI() throws SQLException {
        // Show login screen first
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setVisible(true);
        
        // If login was canceled, exit the application
        if (!loginScreen.isSubmitted()) {
            System.exit(0);
        }
        
        // Get user details from login
        this.userName = loginScreen.getUserName();
        this.userEmail = loginScreen.getUserEmail();
        
        // Initialize the UserController with user details
        UserController userController = new UserController(userName, userEmail, 1000);

        transactionController = new TransactionController();
        transactionController.setUserId(userId);
        setTitle("Financial Budget Gamified - Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Use a BorderLayout for the main frame
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Initialize the UI with the Dashboard tab
        switchTab("Dashboard");
        
        // Center the window on screen
        setLocationRelativeTo(null);
        
        setVisible(true);
    }
    
    /**
     * Creates the top navigation panel with tabs.
     */
    private JPanel createNavigationPanel() {
        // Create a panel with FlowLayout centered, with proper spacing
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        navigationPanel.setBackground(new Color(18, 12, 31)); // Darker background for tabs
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Remove border
        
        // Create tabs with Goals tab between Dashboard and Analytics
        String[] tabNames = {"Dashboard", "Goals", "Analytics", "Quiz", "Leaderboard"};
        
        for (String tabName : tabNames) {
            boolean isSelected = tabName.equals("Dashboard"); // Default to Dashboard selected
            JPanel tabPanel = createTabPanel(tabName, isSelected);
            navigationPanel.add(tabPanel);
            
            // Add tab click listener
            tabPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    // Set all tabs to unselected style
                    for (Component comp : navigationPanel.getComponents()) {
                        if (comp instanceof JPanel) {
                            comp.setBackground(new Color(18, 12, 31));
                            // Update labels inside the panel
                            for (Component inner : ((JPanel)comp).getComponents()) {
                                if (inner instanceof JLabel) {
                                    JLabel label = (JLabel)inner;
                                    if (label.getText() != null && !label.getText().isEmpty() 
                                            && label.getText().length() > 1) {
                                        label.setForeground(new Color(200, 200, 200));
                                    }
                                }
                            }
                        }
                    }
                    
                    // Set clicked tab to selected style
                    tabPanel.setBackground(ACCENT_COLOR);
                    for (Component inner : tabPanel.getComponents()) {
                        if (inner instanceof JLabel) {
                            JLabel label = (JLabel)inner;
                            if (label.getText() != null && !label.getText().isEmpty() 
                                    && label.getText().length() > 1) {
                                label.setForeground(TEXT_COLOR);
                            }
                        }
                    }
                    
                    switchTab(tabName);
                }
            });
        }
        
        // Wrap the navigation panel in a container to ensure it takes full width
        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setBackground(new Color(18, 12, 31));
        navContainer.add(navigationPanel, BorderLayout.CENTER);
        
        return navContainer;
    }
    
    /**
     * Creates a single tab panel for the navigation bar.
     */
    private JPanel createTabPanel(String tabName, boolean isSelected) {
        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.Y_AXIS));
        tabPanel.setBackground(isSelected ? ACCENT_COLOR : new Color(18, 12, 31));
        tabPanel.setPreferredSize(new Dimension(110, 50)); // Smaller height
        tabPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tabPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        // Tab icon
        JLabel iconLabel = new JLabel(getTabIcon(tabName));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setForeground(TEXT_COLOR);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tabPanel.add(iconLabel);
        
        // Add some space between icon and text
        tabPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        
        // Tab name
        JLabel nameLabel = new JLabel(tabName);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(isSelected ? TEXT_COLOR : new Color(200, 200, 200));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 11));
        tabPanel.add(nameLabel);
        
        return tabPanel;
    }
    
    /**
     * Gets the icon text for a tab name.
     */
    private String getTabIcon(String tabName) {
        switch (tabName) {
            case "Dashboard":
                return "📊";
            case "Goals":
                return "🎯";
            case "Analytics":
                return "📈";
            case "Quiz":
                return "❓";
            case "Leaderboard":
                return "🏆";
            case "Log": 
            	return "📝";
            default:
                return "•";
        }
    }
    
    /**
     * Switches to the selected tab.
     */
    private void switchTab(String tabName) {
        System.out.println("Switching to tab: " + tabName);
        
        // Reset the content pane
        getContentPane().removeAll();
        
        // Create navigation panel
        JPanel navigationPanel = createNavigationPanel();
        
        // Create a container panel for the top section that will hold both navigation and header
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(BACKGROUND_COLOR);
        
        // Add navigation panel to the top of the container
        topContainer.add(navigationPanel, BorderLayout.NORTH);
        
        // Always add the top container to the NORTH position
        add(topContainer, BorderLayout.NORTH);
        
        // Handle tab-specific content
        switch (tabName) {
            case "Dashboard":
                // Add header panel below navigation in the top container
                JPanel headerPanel = createHeaderPanel(userName);
                topContainer.add(headerPanel, BorderLayout.CENTER);
                
                // Create main content panel with two sections
                JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
                contentPanel.setBackground(BACKGROUND_COLOR);
                contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
                add(contentPanel, BorderLayout.CENTER);
                
                // Left panel - Transaction logging form
                JPanel logPanel = createTransactionLogPanel();
                contentPanel.add(logPanel);
                
                // Right panel - Transaction history
                JPanel historyPanel = createTransactionHistoryPanel();
                contentPanel.add(historyPanel);
                break;
                
            case "Goals":
                // Add header panel below navigation in the top container
                JPanel goalsHeaderPanel = createHeaderPanel(userName);
                topContainer.add(goalsHeaderPanel, BorderLayout.CENTER);
                
                // Create and add the GoalsUI panel
                GoalsUI goalsUI = new GoalsUI(userId);
                add(goalsUI, BorderLayout.CENTER);
                break;
                
            case "Quiz":
                // For Quiz, don't add a header panel
                UserController userController = new UserController(userName, userEmail, 1000);
                QuizController quizController = new QuizController(userController);
                QuizUI quizUI = new QuizUI(quizController);
                add(quizUI, BorderLayout.CENTER);
                break;
                
            case "Analytics":
                // Add header panel below navigation in the top container
                JPanel analyticsHeaderPanel = createHeaderPanel(userName);
                topContainer.add(analyticsHeaderPanel, BorderLayout.CENTER);
                
                // For now, show a placeholder message
                JPanel analyticsPlaceholderPanel = new JPanel(new BorderLayout());
                analyticsPlaceholderPanel.setBackground(BACKGROUND_COLOR);
                analyticsPlaceholderPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
                
                JLabel analyticsLabel = new JLabel("Analytics Dashboard");
                analyticsLabel.setForeground(TEXT_COLOR);
                analyticsLabel.setFont(new Font("Arial", Font.BOLD, 24));
                analyticsLabel.setHorizontalAlignment(JLabel.CENTER);
                
                JLabel analyticsDescriptionLabel = new JLabel("View your spending patterns and financial insights.");
                analyticsDescriptionLabel.setForeground(new Color(180, 180, 180));
                analyticsDescriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                analyticsDescriptionLabel.setHorizontalAlignment(JLabel.CENTER);
                
                JPanel analyticsTextPanel = new JPanel();
                analyticsTextPanel.setLayout(new BoxLayout(analyticsTextPanel, BoxLayout.Y_AXIS));
                analyticsTextPanel.setBackground(BACKGROUND_COLOR);
                analyticsTextPanel.add(analyticsLabel);
                analyticsTextPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                analyticsTextPanel.add(analyticsDescriptionLabel);
                
                analyticsPlaceholderPanel.add(analyticsTextPanel, BorderLayout.CENTER);
                add(analyticsPlaceholderPanel, BorderLayout.CENTER);
                break;
                
            case "Leaderboard":
                // Add header panel below navigation in the top container
                JPanel leaderboardHeaderPanel = createHeaderPanel(userName);
                topContainer.add(leaderboardHeaderPanel, BorderLayout.CENTER);
                
                // For now, show a placeholder message
                JPanel leaderboardPlaceholderPanel = new JPanel(new BorderLayout());
                leaderboardPlaceholderPanel.setBackground(BACKGROUND_COLOR);
                leaderboardPlaceholderPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
                
                JLabel leaderboardLabel = new JLabel("Leaderboard");
                leaderboardLabel.setForeground(TEXT_COLOR);
                leaderboardLabel.setFont(new Font("Arial", Font.BOLD, 24));
                leaderboardLabel.setHorizontalAlignment(JLabel.CENTER);
                
                JLabel leaderboardDescriptionLabel = new JLabel("See how your progress compares with other users.");
                leaderboardDescriptionLabel.setForeground(new Color(180, 180, 180));
                leaderboardDescriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                leaderboardDescriptionLabel.setHorizontalAlignment(JLabel.CENTER);
                
                JPanel leaderboardTextPanel = new JPanel();
                leaderboardTextPanel.setLayout(new BoxLayout(leaderboardTextPanel, BoxLayout.Y_AXIS));
                leaderboardTextPanel.setBackground(BACKGROUND_COLOR);
                leaderboardTextPanel.add(leaderboardLabel);
                leaderboardTextPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                leaderboardTextPanel.add(leaderboardDescriptionLabel);
                
                leaderboardPlaceholderPanel.add(leaderboardTextPanel, BorderLayout.CENTER);
                add(leaderboardPlaceholderPanel, BorderLayout.CENTER);
                break;
                

            case "Log":
                // Add header panel below navigation in the top container
                JPanel logHeaderPanel = createHeaderPanel(userName);
                topContainer.add(logHeaderPanel, BorderLayout.CENTER);
                
                // Add the LogUI panel
                add(LogUI.getInstance(), BorderLayout.CENTER);
                break;
        }
        
        // Refresh the UI
        revalidate();
        repaint();
    }
    
    private JPanel createHeaderPanel(String userName) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Top section with user info and logout button
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Level 5 Budget Warrior");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topSection.add(titleLabel, BorderLayout.WEST);
        
        // Add logout button
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.addActionListener(e -> logout());
        topSection.add(logoutButton, BorderLayout.EAST);
        
        headerPanel.add(topSection, BorderLayout.NORTH);
        
        // Progress bar panel
        JPanel progressPanel = new JPanel(new BorderLayout(10, 5));
        progressPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel progressLabel = new JLabel("Level Progress");
        progressLabel.setForeground(TEXT_COLOR);
        progressPanel.add(progressLabel, BorderLayout.WEST);
        
        JLabel xpLabel = new JLabel("750 / 1000 XP");
        xpLabel.setForeground(TEXT_COLOR);
        progressPanel.add(xpLabel, BorderLayout.EAST);
        
        JProgressBar progressBar = new JProgressBar(0, 1000);
        progressBar.setValue(750);
        progressBar.setForeground(ACCENT_COLOR);
        progressBar.setBackground(PANEL_COLOR);
        progressPanel.add(progressBar, BorderLayout.SOUTH);
        
        headerPanel.add(progressPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Style a button to match the app's theme
     */
    private void styleButton(JButton button) {
        button.setBackground(ACCENT_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
    
    /**
     * Log out the current user and return to login screen
     */
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            // Dispose current window
            this.dispose();
            
            // Show login screen
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginScreen();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Error opening login screen: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
    
    private JPanel createTransactionLogPanel() {
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        logPanel.setBackground(PANEL_COLOR);
        logPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(PANEL_COLOR.getRed() + 15, PANEL_COLOR.getGreen() + 15, PANEL_COLOR.getBlue() + 15), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Panel title
        JLabel titleLabel = new JLabel("Log a Transaction");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(titleLabel);
        
        logPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Description field
        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionLabel.setForeground(TEXT_COLOR);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(descriptionLabel);
        
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        descriptionField = new JTextField();
        descriptionField.setBackground(FIELD_BACKGROUND);
        descriptionField.setForeground(TEXT_COLOR);
        descriptionField.setCaretColor(TEXT_COLOR);
        descriptionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        descriptionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        descriptionField.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(descriptionField);
        
        logPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Amount field
        JLabel amountLabel = new JLabel("Amount ($)");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        amountLabel.setForeground(TEXT_COLOR);
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(amountLabel);
        
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        amountField = new JTextField();
        amountField.setBackground(FIELD_BACKGROUND);
        amountField.setForeground(TEXT_COLOR);
        amountField.setCaretColor(TEXT_COLOR);
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(amountField);
        
        logPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Date field
        JLabel dateLabel = new JLabel("Date");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(TEXT_COLOR);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(dateLabel);
        
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Create the custom calendar picker
        datePicker = new CustomCalendarPicker();
        datePicker.setBackground(PANEL_COLOR);
        
        // Create a panel for the date picker to set size constraints
        JPanel datePickerPanel = new JPanel(new BorderLayout());
        datePickerPanel.setBackground(PANEL_COLOR);
        datePickerPanel.add(datePicker, BorderLayout.CENTER);
        datePickerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        datePickerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logPanel.add(datePickerPanel);
        
        logPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Transaction type (income/expense)
        JLabel typeLabel = new JLabel("Transaction Type");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        typeLabel.setForeground(TEXT_COLOR);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(typeLabel);
        
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.setBackground(PANEL_COLOR);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        radioPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        incomeButton = new JRadioButton("Income");
        incomeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        incomeButton.setForeground(INCOME_COLOR);
        incomeButton.setBackground(PANEL_COLOR);
        incomeButton.setFocusPainted(false);
        
        expenseButton = new JRadioButton("Expense");
        expenseButton.setFont(new Font("Arial", Font.PLAIN, 14));
        expenseButton.setForeground(EXPENSE_COLOR);
        expenseButton.setBackground(PANEL_COLOR);
        expenseButton.setFocusPainted(false);
        expenseButton.setSelected(true); // Default to expense
        
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(incomeButton);
        typeGroup.add(expenseButton);
        
        radioPanel.add(incomeButton);
        radioPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        radioPanel.add(expenseButton);
        
        logPanel.add(radioPanel);
        
        logPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Category selection
        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryLabel.setForeground(TEXT_COLOR);
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(categoryLabel);
        
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Create category dropdown instead of CategorySelector
        String[] categories = {"Housing", "Food", "Transportation", "Entertainment", "Healthcare", "Education", "Savings", "Income", "Other"};
        categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setBackground(FIELD_BACKGROUND);
        categoryComboBox.setForeground(TEXT_COLOR);
        categoryComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        categoryComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Style the combo box
        categoryComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACCENT_COLOR : FIELD_BACKGROUND);
                setForeground(isSelected ? TEXT_COLOR : TEXT_COLOR);
                return this;
            }
        });
        
        logPanel.add(categoryComboBox);
        
        logPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Log button
        JButton logButton = new JButton("Log Transaction");
        logButton.setBackground(ACCENT_COLOR);
        logButton.setForeground(TEXT_COLOR);
        logButton.setFocusPainted(false);
        logButton.setBorderPainted(false);
        logButton.setFont(new Font("Arial", Font.BOLD, 14));
        logButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logTransaction();
            }
        });
        
        logPanel.add(logButton);
        
        return logPanel;
    }
    
    private JPanel createTransactionHistoryPanel() {
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(PANEL_COLOR);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Header
        JLabel headerLabel = new JLabel("Recent Transactions");
        headerLabel.setForeground(TEXT_COLOR);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Transaction list
        transactionDisplayPanel = new JPanel();
        transactionDisplayPanel.setLayout(new BoxLayout(transactionDisplayPanel, BoxLayout.Y_AXIS));
        transactionDisplayPanel.setBackground(PANEL_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(transactionDisplayPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(PANEL_COLOR);
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        
        // Customize scrollbar
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 80, 140);
                this.trackColor = new Color(24, 15, 41);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        
        // Add components to panel
        historyPanel.add(headerLabel, BorderLayout.NORTH);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Initially populate with transactions
        updateTransactionDisplay();
        
        return historyPanel;
    }

    private void logTransaction() {
        try {
            // Validate inputs
            String description = descriptionField.getText().trim();
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a description for the transaction.");
                return;
            }
            
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount for the transaction.");
                return;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a positive amount.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for the amount.");
                return;
            }
            
            // Get date from CustomCalendarPicker
            Date selectedDate = datePicker.getDate();
            
            // Convert to LocalDate
            LocalDate date = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            // Get transaction type
            boolean isIncome = incomeButton.isSelected();
            
            // Get category safely
            String category = "Other"; // Default category
            if (categoryComboBox != null && categoryComboBox.getSelectedItem() != null) {
                category = categoryComboBox.getSelectedItem().toString();
            }
            
            // Add transaction
            transactionController.addTransaction(description, amount, date, isIncome, category);
            
            // Clear form
            descriptionField.setText("");
            amountField.setText("");
            datePicker.setDate(new Date()); // Reset to today
            expenseButton.setSelected(true); // Reset to expense
            if (categoryComboBox != null) {
                categoryComboBox.setSelectedIndex(0); // Reset to first category
            }
            
            // Update transaction display
            updateTransactionDisplay();
            
            // Show success message
            JOptionPane.showMessageDialog(this, "Transaction logged successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error logging transaction: " + e.getMessage());
        }
    }

    private void updateTransactionDisplay() {
        if (transactionDisplayPanel != null) {
            transactionDisplayPanel.removeAll();
            
            // Get transactions from controller
            List<Transaction> transactions = transactionController.getAllTransactions(userId);
            
            if (transactions.isEmpty()) {
                // Show empty state
                JLabel emptyLabel = new JLabel("No transactions to display");
                emptyLabel.setForeground(TEXT_COLOR);
                emptyLabel.setFont(new Font("Arial", Font.BOLD, 14));
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                transactionDisplayPanel.add(emptyLabel);
            } else {
                // Add each transaction as a card
                for (Transaction transaction : transactions) {
                    JPanel card = createTransactionCard(transaction);
                    transactionDisplayPanel.add(card);
                    
                    // Add spacing between cards
                    transactionDisplayPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
            
            // Make sure scrolling works properly
            transactionDisplayPanel.revalidate();
            transactionDisplayPanel.repaint();
        }
    }
    
    // Create a card for a transaction
    private JPanel createTransactionCard(Transaction transaction) {
        JPanel cardPanel = new JPanel(new BorderLayout(15, 0));
        cardPanel.setBackground(PANEL_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 50, 110), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Create category icon based on transaction category or income status
        JPanel typeIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Determine color and symbol based on transaction type and category
                Color iconColor;
                String iconSymbol;
                if (transaction.isIncome()) {
                    // For income, always use green with a money symbol
                    iconColor = INCOME_COLOR;
                    iconSymbol = "💼"; // Briefcase for salary/income
                } else {
                    // For expenses, use the category color and symbol
                    String category = transaction.getCategory();
                    iconColor = getCategoryColor(category);
                    iconSymbol = getCategorySymbol(category);
                }

                // Draw rounded rectangle background
                g2d.setColor(iconColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Draw the category symbol
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Dialog", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(iconSymbol)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(iconSymbol, x, y);
                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(24, 24);
            }
        };

        // Panel for description and details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(PANEL_COLOR);

        // Description
        JLabel descLabel = new JLabel(transaction.getDescription());
        descLabel.setForeground(TEXT_COLOR);
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Format date as MM/DD/YYYY
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String dateStr = sdf.format(Date.from(transaction.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // Get transaction type and category
        String typeCategory = transaction.isIncome() ? "Income" : transaction.getCategory();

        // Create details line with bullet point separator
        JLabel detailsLabel = new JLabel(dateStr + " • " + typeCategory);
        detailsLabel.setForeground(new Color(180, 180, 180));
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        detailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(descLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(detailsLabel);

        // Amount - format with dollar sign and align right
        JLabel amountLabel = new JLabel(String.format("$%.2f", transaction.getAmount()));
        amountLabel.setForeground(transaction.isIncome() ? INCOME_COLOR : EXPENSE_COLOR);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Create a Delete button
        JButton deleteButton = new JButton("X");
        deleteButton.setForeground(TEXT_COLOR);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 18));
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int transactionId = transaction.getId();
                boolean success = TransactionController.deleteTransaction(transactionId);
                if (success) {
                    // Update the UI to remove the deleted transaction
                    updateTransactionDisplay();
                } else {
                    JOptionPane.showMessageDialog(cardPanel, "Failed to delete transaction", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create a panel for the right side that contains both the amount and the delete button
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.setBackground(PANEL_COLOR);
        eastPanel.add(amountLabel);
        eastPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        eastPanel.add(deleteButton);

        // Create left panel for the type indicator and details
        JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
        leftPanel.setBackground(PANEL_COLOR);
        leftPanel.add(typeIndicator, BorderLayout.WEST);
        leftPanel.add(detailsPanel, BorderLayout.CENTER);

        cardPanel.add(leftPanel, BorderLayout.CENTER);
        cardPanel.add(eastPanel, BorderLayout.EAST);

        return cardPanel;
    }


    // Get color for a category
    private Color getCategoryColor(String category) {
        switch (category) {
            case "Food & Dining": return new Color(255, 87, 51); // Orange-red
            case "Shopping": return new Color(52, 73, 94);      // Dark blue-gray
            case "Utilities": return new Color(155, 89, 182);   // Purple
            case "Housing": return new Color(52, 152, 219);     // Blue
            case "Entertainment": return new Color(155, 89, 182); // Purple
            case "Coffee": return new Color(121, 85, 72);       // Brown
            case "Gifts": return new Color(231, 76, 60);        // Red
            case "Emergency Fund": return new Color(41, 128, 185); // Blue
            case "Transport": return new Color(52, 152, 219);   // Blue
            case "Education": return new Color(241, 196, 15);   // Yellow
            case "Health": return new Color(26, 188, 156);      // Teal
            case "Salary": return new Color(46, 204, 113);      // Green
            case "Food": return new Color(255, 87, 51);         // Orange-red
            case "Bills": return new Color(231, 76, 60);        // Red
            case "Rent": return new Color(22, 160, 133);        // Green
            default: return new Color(149, 165, 166);           // Gray
        }
    }

    // Add method to get category symbol
    private String getCategorySymbol(String category) {
        switch (category) {
            case "Food & Dining": return "🍽️";
            case "Shopping": return "🛍️";
            case "Utilities": return "⚡";
            case "Housing": return "🏠";
            case "Entertainment": return "🎮";
            case "Coffee": return "☕";
            case "Gifts": return "🎁";
            case "Emergency Fund": return "🐷";
            case "Credit Card Debt": return "💳";
            case "Stock Portfolio": return "📈";
            case "Home Down Payment": return "🏡";
            case "Transport": return "🚗";
            case "Education": return "🎓";
            case "Health": return "⚕️";
            case "Salary": return "💼";
            case "Food": return "🍔";
            case "Bills": return "📄";
            case "Rent": return "🏢";
            default: return "•";
        }
    }

    // Add a helper method to set colors for components recursively
    private void setComponentColors(Component comp) {
        if (comp instanceof JPanel) {
            comp.setBackground(PANEL_COLOR);
            for (Component child : ((JPanel) comp).getComponents()) {
                setComponentColors(child);
            }
        } else if (comp instanceof JButton) {
            comp.setBackground(ACCENT_COLOR);
            comp.setForeground(TEXT_COLOR);
        } else if (comp instanceof JLabel) {
            comp.setForeground(TEXT_COLOR);
        } else if (comp instanceof JTextField) {
            comp.setBackground(FIELD_BACKGROUND);
            comp.setForeground(TEXT_COLOR);
        } else if (comp instanceof JComboBox) {
            comp.setBackground(FIELD_BACKGROUND);
            comp.setForeground(TEXT_COLOR);
        }
    }

    /**
     * Format dates for the JDatePicker
     */
    private class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private String datePattern = "MM/dd/yyyy";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
        
        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            return dateFormatter.parse(text);
        }
        
        @Override
        public String valueToString(Object value) throws java.text.ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }

    /**
     * Custom CategorySelector component with dropdown
     */
    private class CategorySelector extends JPanel {
        private JTextField categoryField;
        private JDialog categoryDialog;
        private String selectedCategory;
        private final String[] categories = {
            "Food & Dining", "Shopping", "Utilities", "Housing", 
            "Entertainment", "Coffee", "Gifts", "Emergency Fund",
            "Credit Card Debt", "Stock Portfolio", "Home Down Payment", 
            "Transport", "Education", "Health", "Salary", "Other"
        };
        
        // Map categories to their goal status
        private final java.util.Map<String, Boolean> goalCategories = new java.util.HashMap<>();
        
        public CategorySelector() {
            setLayout(new BorderLayout());
            
            // Set up goal categories
            goalCategories.put("Emergency Fund", true);
            goalCategories.put("Credit Card Debt", true);
            goalCategories.put("Stock Portfolio", true);
            goalCategories.put("Home Down Payment", true);
            
            // Default selected category
            selectedCategory = "Food & Dining";
            
            // Create category field
            categoryField = new JTextField(selectedCategory);
            categoryField.setEditable(false);
            categoryField.setBackground(FIELD_BACKGROUND);
            categoryField.setForeground(TEXT_COLOR);
            categoryField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            categoryField.setHorizontalAlignment(JTextField.CENTER);
            
            // Create dropdown button
            JButton dropdownButton = new JButton("▼");
            dropdownButton.setBackground(ACCENT_COLOR);
            dropdownButton.setForeground(TEXT_COLOR);
            dropdownButton.setFocusPainted(false);
            dropdownButton.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            
            // Add action listener to show category dialog
            dropdownButton.addActionListener(e -> showCategoryDialog());
            
            // Add components to this panel
            add(categoryField, BorderLayout.CENTER);
            add(dropdownButton, BorderLayout.EAST);
        }
        
        private void showCategoryDialog() {
            if (categoryDialog != null && categoryDialog.isVisible()) {
                categoryDialog.dispose();
                return;
            }
            
            // Create dialog
            categoryDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Category", true);
            categoryDialog.setUndecorated(true);
            
            // Create main panel
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createLineBorder(new Color(70, 50, 110), 1));
            mainPanel.setBackground(new Color(24, 15, 41)); // darker background for the dialog
            
            // Title panel
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
            titlePanel.setBackground(new Color(40, 24, 69));
            titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            JLabel titleLabel = new JLabel("Category");
            titleLabel.setForeground(TEXT_COLOR);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            
            // Close button
            JButton closeButton = new JButton("×");
            closeButton.setForeground(TEXT_COLOR);
            closeButton.setBackground(null);
            closeButton.setBorder(null);
            closeButton.setFocusPainted(false);
            closeButton.setFont(new Font("Arial", Font.BOLD, 18));
            closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            closeButton.addActionListener(e -> categoryDialog.dispose());
            
            titlePanel.add(titleLabel);
            titlePanel.add(Box.createHorizontalGlue());
            titlePanel.add(closeButton);
            
            // Add title panel
            JPanel wrapTitlePanel = new JPanel(new BorderLayout());
            wrapTitlePanel.setBackground(null);
            wrapTitlePanel.add(titlePanel, BorderLayout.NORTH);
            mainPanel.add(wrapTitlePanel);
            
            // Grid panel for categories - using 3 columns instead of 4 to better match the screenshot
            JPanel gridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
            gridPanel.setBackground(new Color(24, 15, 41));
            gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Add category options
            for (String category : categories) {
                JPanel categoryOption = createCategoryIconPanel(category, goalCategories.getOrDefault(category, false));
                gridPanel.add(categoryOption);
            }
            
            // Add grid to a scroll pane in case there are many categories
            JScrollPane scrollPane = new JScrollPane(gridPanel);
            scrollPane.setBackground(new Color(24, 15, 41));
            scrollPane.setBorder(null);
            // Set preferred size to control the dialog dimensions
            scrollPane.setPreferredSize(new Dimension(400, 400));
            // Disable horizontal scrollbar
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
                @Override
                protected void configureScrollBarColors() {
                    this.thumbColor = new Color(100, 80, 140);
                    this.trackColor = new Color(24, 15, 41);
                }
            });
            
            mainPanel.add(scrollPane);
            
            // Set dialog properties
            categoryDialog.getContentPane().add(mainPanel);
            categoryDialog.pack();
            // Ensure dialog has a fixed width that fits 3 columns
            categoryDialog.setSize(400, 500);
            
            // Position dialog centered on screen
            categoryDialog.setLocationRelativeTo(this);
            
            categoryDialog.setVisible(true);
        }
        
        private JPanel createCategoryIconPanel(String category, boolean isGoal) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(new Color(40, 24, 69));
            panel.setBorder(BorderFactory.createLineBorder(new Color(70, 50, 110), 1, true));
            panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            panel.setPreferredSize(new Dimension(100, 110));
            
            // Category icon
            JPanel iconBackground = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw colored circle based on category
                    g2d.setColor(getCategoryColor(category));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    
                    // Draw icon
                    Font iconFont = new Font("Dialog", Font.BOLD, 20);
                    g2d.setFont(iconFont);
                    g2d.setColor(Color.WHITE);
                    String iconSymbol = getCategorySymbol(category);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = (getWidth() - fm.stringWidth(iconSymbol)) / 2;
                    int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(iconSymbol, textX, textY);
                    
                    g2d.dispose();
                }
            };
            
            iconBackground.setPreferredSize(new Dimension(60, 60));
            iconBackground.setMaximumSize(new Dimension(60, 60));
            iconBackground.setMinimumSize(new Dimension(60, 60));
            iconBackground.setBackground(new Color(40, 24, 69));
            iconBackground.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Category name
            JLabel nameLabel = new JLabel(category);
            nameLabel.setForeground(TEXT_COLOR);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            nameLabel.setHorizontalAlignment(JLabel.CENTER);
            
            // Goal label if applicable
            JLabel goalLabel = null;
            if (isGoal) {
                goalLabel = new JLabel("Goal category");
                goalLabel.setForeground(new Color(150, 150, 150));
                goalLabel.setFont(new Font("Arial", Font.ITALIC, 10));
                goalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                goalLabel.setHorizontalAlignment(JLabel.CENTER);
            }
            
            // Add spacing around components
            JPanel iconPanel = new JPanel();
            iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.Y_AXIS));
            iconPanel.setBackground(new Color(40, 24, 69));
            iconPanel.add(Box.createVerticalStrut(5));
            iconPanel.add(iconBackground);
            iconPanel.add(Box.createVerticalStrut(5));
            iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            panel.add(Box.createVerticalStrut(5));
            panel.add(iconPanel);
            panel.add(nameLabel);
            if (goalLabel != null) {
                panel.add(Box.createRigidArea(new Dimension(0, 2)));
                panel.add(goalLabel);
            }
            panel.add(Box.createVerticalStrut(5));
            
            // Add hover effect and click listener
            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    panel.setBackground(new Color(70, 50, 110));
                    iconBackground.setBackground(new Color(70, 50, 110));
                    iconPanel.setBackground(new Color(70, 50, 110));
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    panel.setBackground(new Color(40, 24, 69));
                    iconBackground.setBackground(new Color(40, 24, 69));
                    iconPanel.setBackground(new Color(40, 24, 69));
                }
                
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    selectedCategory = category;
                    categoryField.setText(category);
                    categoryDialog.dispose();
                }
            });
            
            return panel;
        }
        
        private ImageIcon getCategoryIcon(String category) {
            // Create a buffered image for the icon
            BufferedImage image = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw background
            g2.setColor(getCategoryColor(category));
            g2.fillRoundRect(0, 0, 60, 60, 10, 10);
            
            // Draw symbol
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Dialog", Font.BOLD, 24));
            String symbol = getCategorySymbol(category);
            FontMetrics fm = g2.getFontMetrics();
            int x = (60 - fm.stringWidth(symbol)) / 2;
            int y = ((60 - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(symbol, x, y);
            
            g2.dispose();
            return new ImageIcon(image);
        }
        
        private String getCategorySymbol(String category) {
            // Remove this duplicate method as we've moved it to the outer class
            return CalendarUI.this.getCategorySymbol(category);
        }
        
        private Color getCategoryColor(String category) {
            // Use the outer class method instead
            return CalendarUI.this.getCategoryColor(category);
        }
        
        public String getSelectedCategory() {
            return selectedCategory;
        }
        
        public void setSelectedCategory(String category) {
            for (String validCategory : categories) {
                if (validCategory.equals(category)) {
                    selectedCategory = category;
                    categoryField.setText(category);
                    break;
                }
            }
        }
        
        @Override
        public void setBackground(Color bg) {
            super.setBackground(bg);
            if (categoryField != null) {
                categoryField.setBackground(bg);
            }
        }
        
        @Override
        public void setForeground(Color fg) {
            super.setForeground(fg);
            if (categoryField != null) {
                categoryField.setForeground(fg);
            }
        }
    }

    // Add this helper method after the setComponentColors method
    private void customizeCalendarComponents(JComponent comp) {
        comp.setBackground(PANEL_COLOR);
        comp.setForeground(TEXT_COLOR);
        
        // If it's a table (the calendar grid)
        if (comp instanceof JTable) {
            JTable table = (JTable) comp;
            table.setBackground(PANEL_COLOR);
            table.setForeground(TEXT_COLOR);
            table.setGridColor(new Color(70, 50, 110));
            table.setSelectionBackground(ACCENT_COLOR);
            table.setSelectionForeground(TEXT_COLOR);
            
            // Set cell renderer to customize cell appearance
            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, 
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column);
                    
                    if (c instanceof JComponent) {
                        ((JComponent) c).setBorder(BorderFactory.createEmptyBorder());
                    }
                    
                    // Set background for today's date
                    if (value != null && value.toString().equals(Integer.toString(LocalDate.now().getDayOfMonth()))
                            && !isSelected) {
                        c.setBackground(new Color(60, 35, 100));
                    } else if (!isSelected) {
                        c.setBackground(PANEL_COLOR);
                    }
                    
                    // Set text alignment
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                    
                    return c;
                }
            });
        }
        
        // Apply to all child components
        for (Component child : comp.getComponents()) {
            if (child instanceof JComponent) {
                customizeCalendarComponents((JComponent) child);
            }
        }
    }

    /**
     * @deprecated This method has been replaced by app.Main.main().
     * Please use app.Main.main() as the main entry point for the application.
     */
    @Deprecated
    public static void main(String[] args) throws SQLException {
        System.out.println("This main method is deprecated. Please use app.Main.main() instead.");
        // Forward to the new main method
        app.Main.main(args);
    }
}
