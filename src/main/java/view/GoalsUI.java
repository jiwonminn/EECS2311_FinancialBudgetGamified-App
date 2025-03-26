package view;

import controller.GoalController;
import controller.CategoryManager;
import model.Goal;
import view.CustomCalendarPicker;
import utils.EmailNotifier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.Properties;
import javax.swing.SpinnerDateModel;
import java.util.Arrays;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GoalsUI extends JPanel {
    // Define colors to match the existing UI
    private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    private final Color PANEL_COLOR = new Color(40, 24, 69);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color ACCENT_COLOR = new Color(128, 90, 213);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color WARNING_COLOR = new Color(235, 149, 50); 
    private final Color DANGER_COLOR = new Color(215, 38, 61);
    private final Color FIELD_BACKGROUND = new Color(50, 35, 80);

    private GoalController goalController;
    private CategoryManager categoryManager;
    private JPanel goalsPanel;
    private int userId;
    private String userName;
    private String userEmail;
    private JDialog addGoalDialog;
    
    // Common categories for goals
    private final String[] goalCategories = {
        "Savings", "Housing", "Food", "Transportation", "Entertainment", 
        "Healthcare", "Education", "Investment", "Travel", "All Categories"
    };
    
    public GoalsUI(int userId) {
        this(userId, "User", "");
    }
    
    public GoalsUI(int userId, String userName, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;

        goalController = new GoalController();
        categoryManager = CategoryManager.getInstance();


        initializeUI();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add title at top
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Financial Goals");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Create a panel for the buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        
        // Add Email Progress Report button
        JButton emailReportButton = new JButton("Email Progress Report");
        styleButton(emailReportButton);
        emailReportButton.setIcon(createEmailIcon());
        emailReportButton.addActionListener(e -> sendGoalProgressEmail());
        buttonsPanel.add(emailReportButton);
        
        // Add New Goal button
        JButton addGoalButton = new JButton("Add New Goal");
        styleButton(addGoalButton);
        addGoalButton.addActionListener(e -> showAddGoalDialog());
        buttonsPanel.add(addGoalButton);
        
        titlePanel.add(buttonsPanel, BorderLayout.EAST);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Goals container with scroll
        goalsPanel = new JPanel();
        goalsPanel.setLayout(new BoxLayout(goalsPanel, BoxLayout.Y_AXIS));
        goalsPanel.setBackground(BACKGROUND_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(goalsPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Load goals
        loadGoals();
    }
    
    /**
     * Load and display goals for the current user
     */
    private void loadGoals() {
        goalsPanel.removeAll();
        
        try {
            List<Goal> goals = goalController.getGoalsByUserId(userId);
            
            if (goals.isEmpty()) {
                // Show empty state
                JPanel emptyStatePanel = new JPanel();
                emptyStatePanel.setLayout(new BoxLayout(emptyStatePanel, BoxLayout.Y_AXIS));
                emptyStatePanel.setBackground(BACKGROUND_COLOR);
                emptyStatePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                emptyStatePanel.setBorder(new EmptyBorder(50, 0, 0, 0));
                
                JLabel emptyIcon = new JLabel("🎯");
                emptyIcon.setFont(new Font("Arial", Font.PLAIN, 48));
                emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                JLabel emptyLabel = new JLabel("No financial goals yet");
                emptyLabel.setFont(new Font("Arial", Font.BOLD, 18));
                emptyLabel.setForeground(TEXT_COLOR);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                JLabel emptyHintLabel = new JLabel("Create your first financial goal to start tracking your progress");
                emptyHintLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                emptyHintLabel.setForeground(new Color(180, 180, 180));
                emptyHintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                JButton createGoalButton = new JButton("Create a Goal");
                styleButton(createGoalButton);
                createGoalButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                createGoalButton.addActionListener(e -> showAddGoalDialog());
                
                emptyStatePanel.add(emptyIcon);
                emptyStatePanel.add(Box.createRigidArea(new Dimension(0, 15)));
                emptyStatePanel.add(emptyLabel);
                emptyStatePanel.add(Box.createRigidArea(new Dimension(0, 5)));
                emptyStatePanel.add(emptyHintLabel);
                emptyStatePanel.add(Box.createRigidArea(new Dimension(0, 20)));
                emptyStatePanel.add(createGoalButton);
                
                goalsPanel.add(emptyStatePanel);
            } else {
                // Display goals
                for (Goal goal : goals) {
                    JPanel goalCard = createGoalCard(goal);
                    goalsPanel.add(goalCard);
                    goalsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                }
            }
            
            revalidate();
            repaint();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading goals: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Creates a visual card for a goal
     */
    private JPanel createGoalCard(Goal goal) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(PANEL_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(PANEL_COLOR.getRed() + 15, PANEL_COLOR.getGreen() + 15, PANEL_COLOR.getBlue() + 15), 1),
                new EmptyBorder(15, 15, 15, 15)));
        
        // Goal title and amount section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel(goal.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel amountLabel = new JLabel(String.format("$%.2f / $%.2f", goal.getCurrentAmount(), goal.getTargetAmount()));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        amountLabel.setForeground(TEXT_COLOR);
        headerPanel.add(amountLabel, BorderLayout.EAST);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Goal details section
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(PANEL_COLOR);
        detailsPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Left side with description and dates
        JPanel leftDetailsPanel = new JPanel();
        leftDetailsPanel.setLayout(new BoxLayout(leftDetailsPanel, BoxLayout.Y_AXIS));
        leftDetailsPanel.setBackground(PANEL_COLOR);
        
        // Description
        if (goal.getDescription() != null && !goal.getDescription().isEmpty()) {
            JLabel descLabel = new JLabel(goal.getDescription());
            descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            descLabel.setForeground(new Color(200, 200, 200));
            leftDetailsPanel.add(descLabel);
            leftDetailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Date range
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        JLabel dateRangeLabel = new JLabel("Timeline: " + dateFormat.format(goal.getStartDate()) + 
                " to " + dateFormat.format(goal.getTargetDate()));
        dateRangeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        dateRangeLabel.setForeground(new Color(180, 180, 180));
        leftDetailsPanel.add(dateRangeLabel);
        
        // Days remaining
        long daysRemaining = goal.getDaysRemaining();
        String timeText = daysRemaining > 0 ? 
                daysRemaining + " days remaining" :
                "Goal period ended";
        JLabel timeRemainingLabel = new JLabel(timeText);
        timeRemainingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeRemainingLabel.setForeground(daysRemaining > 0 ? new Color(180, 180, 180) : WARNING_COLOR);
        leftDetailsPanel.add(timeRemainingLabel);
        
        detailsPanel.add(leftDetailsPanel, BorderLayout.WEST);
        
        // Right side with category
        JPanel rightDetailsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightDetailsPanel.setBackground(PANEL_COLOR);
        
        JLabel categoryLabel = new JLabel(goal.getCategory());
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryLabel.setForeground(TEXT_COLOR);
        categoryLabel.setOpaque(true);
        categoryLabel.setBackground(ACCENT_COLOR);
        categoryLabel.setBorder(new EmptyBorder(3, 8, 3, 8));
        rightDetailsPanel.add(categoryLabel);
        
        detailsPanel.add(rightDetailsPanel, BorderLayout.EAST);
        
        cardPanel.add(detailsPanel, BorderLayout.CENTER);
        
        // Progress bar section
        JPanel progressPanel = new JPanel(new BorderLayout(10, 0));
        progressPanel.setBackground(PANEL_COLOR);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) goal.getProgressPercentage());
        progressBar.setStringPainted(false);
        progressBar.setBackground(new Color(60, 45, 90));
        
        // Set color based on progress and status
        if (goal.isCompleted()) {
            progressBar.setForeground(SUCCESS_COLOR);
        } else if (goal.isAtRisk()) {
            progressBar.setForeground(DANGER_COLOR);
        } else {
            progressBar.setForeground(ACCENT_COLOR);
        }
        
        JPanel barWrapperPanel = new JPanel(new BorderLayout());
        barWrapperPanel.setBackground(PANEL_COLOR);
        barWrapperPanel.add(progressBar, BorderLayout.CENTER);
        
        progressPanel.add(barWrapperPanel, BorderLayout.CENTER);
        
        // Progress percentage
        JLabel progressLabel = new JLabel(String.format("%.0f%%", goal.getProgressPercentage()));
        progressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        if (goal.isCompleted()) {
            progressLabel.setForeground(SUCCESS_COLOR);
        } else if (goal.isAtRisk()) {
            progressLabel.setForeground(DANGER_COLOR);
        } else {
            progressLabel.setForeground(TEXT_COLOR);
        }
        
        progressPanel.add(progressLabel, BorderLayout.EAST);
        
        cardPanel.add(progressPanel, BorderLayout.SOUTH);
        
        // Add action buttons
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem updateProgressItem = new JMenuItem("Update Progress");
        updateProgressItem.addActionListener(e -> updateGoalProgress(goal));
        popupMenu.add(updateProgressItem);
        
        JMenuItem editItem = new JMenuItem("Edit Goal");
        editItem.addActionListener(e -> editGoal(goal));
        popupMenu.add(editItem);
        
        popupMenu.addSeparator();
        
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.setForeground(DANGER_COLOR);
        deleteItem.addActionListener(e -> deleteGoal(goal));
        popupMenu.add(deleteItem);
        
        // Add right-click menu
        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) { // Right-click
                    popupMenu.show(cardPanel, e.getX(), e.getY());
                }
            }
        });
        
        return cardPanel;
    }
    
    /**
     * Show dialog to add a new goal
     */
    private void showAddGoalDialog() {
        if (addGoalDialog != null && addGoalDialog.isVisible()) {
            addGoalDialog.toFront();
            return;
        }
        
        addGoalDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Goal", true);
        addGoalDialog.setLayout(new BorderLayout());
        addGoalDialog.getContentPane().setBackground(PANEL_COLOR);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(PANEL_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title field
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PANEL_COLOR);
        titlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Goal Title:");
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        JTextField titleField = new JTextField(20);
        styleTextField(titleField);
        titlePanel.add(titleField, BorderLayout.CENTER);
        
        formPanel.add(titlePanel);
        
        // Description field
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(PANEL_COLOR);
        descPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel descLabel = new JLabel("Description (optional):");
        descLabel.setForeground(TEXT_COLOR);
        descPanel.add(descLabel, BorderLayout.NORTH);
        
        JTextField descField = new JTextField(20);
        styleTextField(descField);
        descPanel.add(descField, BorderLayout.CENTER);
        
        formPanel.add(descPanel);
        
        // Target amount field
        JPanel amountPanel = new JPanel(new BorderLayout());
        amountPanel.setBackground(PANEL_COLOR);
        amountPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel amountLabel = new JLabel("Target Amount ($):");
        amountLabel.setForeground(TEXT_COLOR);
        amountPanel.add(amountLabel, BorderLayout.NORTH);
        
        JTextField amountField = new JTextField(20);
        styleTextField(amountField);
        amountPanel.add(amountField, BorderLayout.CENTER);
        
        formPanel.add(amountPanel);
        
        // Category field
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBackground(PANEL_COLOR);
        categoryPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setForeground(TEXT_COLOR);
        categoryPanel.add(categoryLabel, BorderLayout.NORTH);
        
        // Add "Create Custom Category" option to the goal categories
        String[] displayCategories = Arrays.copyOf(goalCategories, goalCategories.length + 1);
        displayCategories[displayCategories.length - 1] = "Create Custom Category...";
        
        JComboBox<String> categoryComboBox = new JComboBox<>(displayCategories);
        categoryComboBox.setBackground(FIELD_BACKGROUND);
        categoryComboBox.setForeground(TEXT_COLOR);
        ((JComponent) categoryComboBox.getRenderer()).setOpaque(true);
        categoryPanel.add(categoryComboBox, BorderLayout.CENTER);
        
        // Add listener for custom category creation
        categoryComboBox.addActionListener(e -> {
            if ("Create Custom Category...".equals(categoryComboBox.getSelectedItem())) {
                String customCategory = JOptionPane.showInputDialog(
                    addGoalDialog, 
                    "Enter a custom category name:",
                    "Create Custom Category",
                    JOptionPane.PLAIN_MESSAGE
                );
                
                if (customCategory != null && !customCategory.trim().isEmpty()) {
                    // Add to combo box and select it
                    categoryComboBox.removeItem("Create Custom Category...");
                    categoryComboBox.addItem(customCategory);
                    categoryComboBox.addItem("Create Custom Category...");
                    categoryComboBox.setSelectedItem(customCategory);
                } else {
                    // Revert to first item if canceled or empty
                    categoryComboBox.setSelectedIndex(0);
                }
            }
        });
        
        formPanel.add(categoryPanel);
        
        // Date pickers
        JPanel datesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        datesPanel.setBackground(PANEL_COLOR);
        datesPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Start date
        JPanel startDatePanel = new JPanel(new BorderLayout());
        startDatePanel.setBackground(PANEL_COLOR);
        
        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setForeground(TEXT_COLOR);
        startDatePanel.add(startDateLabel, BorderLayout.NORTH);
        
        // Create a custom calendar picker for start date
        CustomCalendarPicker startDatePicker = new CustomCalendarPicker();
        startDatePanel.add(startDatePicker, BorderLayout.CENTER);
        
        datesPanel.add(startDatePanel);
        
        // Target date
        JPanel targetDatePanel = new JPanel(new BorderLayout());
        targetDatePanel.setBackground(PANEL_COLOR);
        
        JLabel targetDateLabel = new JLabel("Target Date:");
        targetDateLabel.setForeground(TEXT_COLOR);
        targetDatePanel.add(targetDateLabel, BorderLayout.NORTH);
        
        // Create a custom calendar picker for target date
        Calendar targetCal = Calendar.getInstance();
        targetCal.add(Calendar.MONTH, 1); // Default to one month from now
        CustomCalendarPicker targetDatePicker = new CustomCalendarPicker(targetCal);
        targetDatePanel.add(targetDatePicker, BorderLayout.CENTER);
        
        datesPanel.add(targetDatePanel);
        
        formPanel.add(datesPanel);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(PANEL_COLOR);
        
        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton);
        cancelButton.addActionListener(e -> addGoalDialog.dispose());
        
        JButton saveButton = new JButton("Create Goal");
        styleButton(saveButton);
        saveButton.setBackground(SUCCESS_COLOR);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                String title = titleField.getText().trim();
                String description = descField.getText().trim();
                String amountText = amountField.getText().trim();
                String category = (String) categoryComboBox.getSelectedItem();
                Date startDate = startDatePicker.getDate();
                Date targetDate = targetDatePicker.getDate();
                
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(addGoalDialog, "Please enter a title for your goal.");
                    return;
                }
                
                double amount;
                try {
                    amount = Double.parseDouble(amountText);
                    if (amount <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addGoalDialog, "Please enter a valid positive amount.");
                    return;
                }
                
                if (targetDate.before(startDate)) {
                    JOptionPane.showMessageDialog(addGoalDialog, "Target date must be after start date.");
                    return;
                }
                
                // Get the selected category
                if (category == null || category.equals("Create Custom Category...")) {
                    category = "Other";
                }
                
                // Create the goal
                Goal newGoal = new Goal(userId, title, description, amount, startDate, targetDate, category);
                
                try {
                    goalController.createGoal(newGoal);
                    
                    // Add the category to the CategoryManager so it appears in transaction dropdown
                    categoryManager.addCategory(category);
                    
                    addGoalDialog.dispose();
                    loadGoals(); // Refresh the list
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(addGoalDialog,
                            "Error creating goal: " + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        formPanel.add(buttonPanel);
        
        addGoalDialog.add(formPanel, BorderLayout.CENTER);
        addGoalDialog.pack();
        addGoalDialog.setLocationRelativeTo(this);
        addGoalDialog.setVisible(true);
    }
    
    /**
     * Update a goal's progress
     */
    private void updateGoalProgress(Goal goal) {
        try {
            // Automatically update based on transactions
            boolean success = goalController.updateGoalProgress(goal.getId());
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Goal progress updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadGoals(); // Refresh the view
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not update goal progress.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating goal: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Edit an existing goal
     */
    private void editGoal(Goal goal) {
        // For now, just show a message
        JOptionPane.showMessageDialog(this,
                "Goal editing will be implemented in a future update.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Delete a goal
     */
    private void deleteGoal(Goal goal) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the goal: " + goal.getTitle() + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = goalController.deleteGoal(goal.getId());
                
                if (success) {
                    loadGoals(); // Refresh the view
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Could not delete goal.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error deleting goal: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Style a button to match the UI
     */
    private void styleButton(JButton button) {
        button.setBackground(ACCENT_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }
    
    /**
     * Style a text field to match the UI
     */
    private void styleTextField(JTextField textField) {
        textField.setBackground(FIELD_BACKGROUND);
        textField.setForeground(TEXT_COLOR);
        textField.setCaretColor(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(70, 50, 110), 1),
                new EmptyBorder(8, 10, 8, 10)));
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
    }
    
    /**
     * Send an email with the goal progress report
     */
    private void sendGoalProgressEmail() {
        try {
            // Get all goals for the user
            List<Goal> goals = goalController.getGoalsByUserId(userId);
            
            if (goals.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "You don't have any goals to report on yet.",
                    "No Goals Found",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Get the user's email address
            String userEmailInput = getUserEmail();
            if (userEmailInput == null || userEmailInput.trim().isEmpty()) {
                userEmailInput = JOptionPane.showInputDialog(this,
                    "Please enter your email address to receive the report:",
                    "Email Address Required",
                    JOptionPane.PLAIN_MESSAGE);
                
                if (userEmailInput == null || userEmailInput.trim().isEmpty()) {
                    return; // User canceled
                }
                
                // Optional: Validate email format
                if (!userEmailInput.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    JOptionPane.showMessageDialog(this,
                        "Please enter a valid email address.",
                        "Invalid Email",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Create a final copy of the email for use in the lambda
            final String userEmail = userEmailInput;
            final String userName = getUserName();
            
            // Show a progress dialog
            JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sending Email", true);
            progressDialog.setLayout(new BorderLayout());
            progressDialog.setSize(300, 100);
            progressDialog.setLocationRelativeTo(this);
            
            JLabel progressLabel = new JLabel("Sending progress report to " + userEmail + "...");
            progressLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
            progressLabel.setHorizontalAlignment(JLabel.CENTER);
            progressDialog.add(progressLabel, BorderLayout.CENTER);
            
            // Start email sending in a background thread
            new Thread(() -> {
                boolean success = false;
                try {
                    success = EmailNotifier.sendGoalProgressEmail(userEmail, userName, goals);
                } finally {
                    boolean finalSuccess = success;
                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();
                        if (finalSuccess) {
                            JOptionPane.showMessageDialog(this,
                                "Goal progress report sent to " + userEmail,
                                "Email Sent",
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                "Failed to send the progress report. Please try again later.",
                                "Email Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            }).start();
            
            // Show the progress dialog
            progressDialog.setVisible(true);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error retrieving goals: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Get the user's email address
     */
    private String getUserEmail() {
        return userEmail;
    }
    
    /**
     * Get the user's name
     */
    private String getUserName() {
        return userName;
    }
    
    /**
     * Create an email icon for the button
     */
    private ImageIcon createEmailIcon() {
        // Create a 16x16 image for the icon
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Set rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw envelope body
        g2d.setColor(Color.WHITE);
        g2d.fillRect(1, 3, 14, 10);
        
        // Draw envelope border
        g2d.setColor(Color.WHITE);
        g2d.drawRect(1, 3, 14, 10);
        
        // Draw envelope flap
        int[] xPoints = {1, 8, 15};
        int[] yPoints = {3, 9, 3};
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Draw line at bottom of flap
        g2d.setColor(Color.WHITE);
        g2d.drawLine(1, 3, 15, 3);
        
        g2d.dispose();
        
        return new ImageIcon(image);
    }
} 