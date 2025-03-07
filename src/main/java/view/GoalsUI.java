package view;

import model.UserGoal;
import controller.GoalController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.table.TableCellRenderer;

/**
 * UI for the Goals tab
 */
public class GoalsUI extends JPanel {
    
    // Colors
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 250);
    private static final Color PANEL_COLOR = new Color(255, 255, 255);
    private static final Color PRIMARY_COLOR = new Color(100, 100, 200);
    private static final Color SUCCESS_COLOR = new Color(80, 200, 120);
    private static final Color WARNING_COLOR = new Color(255, 180, 50);
    private static final Color DANGER_COLOR = new Color(220, 80, 80);
    
    // Controller
    private GoalController goalController;
    
    // UI Components
    private JPanel savingGoalsPanel;
    private JPanel spendingGoalsPanel;
    private JTable savingGoalsTable;
    private JTable spendingGoalsTable;
    private DefaultTableModel savingGoalsModel;
    private DefaultTableModel spendingGoalsModel;
    
    /**
     * Constructor
     * @param goalController the goal controller
     */
    public GoalsUI(GoalController goalController) {
        this.goalController = goalController;
        initializeUI();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Financial Goals");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshGoals());
        titlePanel.add(refreshButton, BorderLayout.EAST);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Main content panel with two sections (saving and spending goals)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Saving goals panel
        savingGoalsPanel = createSavingGoalsPanel();
        contentPanel.add(savingGoalsPanel);
        
        // Spending goals panel
        spendingGoalsPanel = createSpendingGoalsPanel();
        contentPanel.add(spendingGoalsPanel);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Load initial goals data
        refreshGoals();
    }
    
    /**
     * Creates the saving goals panel
     * @return the saving goals panel
     */
    private JPanel createSavingGoalsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)));
        
        // Panel title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Saving Goals");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(SUCCESS_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton addButton = new JButton("Add Goal");
        addButton.setBackground(SUCCESS_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> showAddSavingGoalDialog());
        headerPanel.add(addButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table for saving goals
        String[] columnNames = {"Goal Name", "Target ($)", "Current ($)", "Progress", "Days Left", "Actions"};
        savingGoalsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only the actions column is editable
            }
        };
        
        savingGoalsTable = new JTable(savingGoalsModel);
        savingGoalsTable.setRowHeight(35);
        savingGoalsTable.setShowGrid(true);
        savingGoalsTable.getTableHeader().setReorderingAllowed(false);
        savingGoalsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        savingGoalsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        
        // Set up column renderers
        setUpTableRenderers(savingGoalsTable);
        
        // Scrollable panel for the table
        JScrollPane scrollPane = new JScrollPane(savingGoalsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the spending goals panel
     * @return the spending goals panel
     */
    private JPanel createSpendingGoalsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)));
        
        // Panel title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Spending Goals (Budgets)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(WARNING_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton addButton = new JButton("Add Budget");
        addButton.setBackground(WARNING_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> showAddSpendingGoalDialog());
        headerPanel.add(addButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table for spending goals
        String[] columnNames = {"Budget Name", "Limit ($)", "Spent ($)", "Status", "Days Left", "Actions"};
        spendingGoalsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only the actions column is editable
            }
        };
        
        spendingGoalsTable = new JTable(spendingGoalsModel);
        spendingGoalsTable.setRowHeight(35);
        spendingGoalsTable.setShowGrid(true);
        spendingGoalsTable.getTableHeader().setReorderingAllowed(false);
        spendingGoalsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        spendingGoalsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        
        // Set up column renderers
        setUpTableRenderers(spendingGoalsTable);
        
        // Scrollable panel for the table
        JScrollPane scrollPane = new JScrollPane(spendingGoalsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Sets up table renderers for customizing cell display
     * @param table the table to customize
     */
    private void setUpTableRenderers(JTable table) {
        // Center align all columns except the last one
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Custom renderer for the progress/status column
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                
                String valueStr = value.toString();
                if (valueStr.contains("Completed") || valueStr.contains("Good")) {
                    label.setForeground(SUCCESS_COLOR);
                } else if (valueStr.contains("Warning") || valueStr.contains("Close")) {
                    label.setForeground(WARNING_COLOR);
                } else if (valueStr.contains("Exceeded") || valueStr.contains("Behind")) {
                    label.setForeground(DANGER_COLOR);
                } else {
                    label.setForeground(table.getForeground());
                }
                
                return label;
            }
        });
        
        // Action buttons column
        ButtonRenderer buttonRenderer = new ButtonRenderer();
        ButtonEditor buttonEditor = new ButtonEditor(new JCheckBox());
        table.getColumnModel().getColumn(5).setCellRenderer(buttonRenderer);
        table.getColumnModel().getColumn(5).setCellEditor(buttonEditor);
    }
    
    /**
     * Shows dialog for adding a new saving goal
     */
    private void showAddSavingGoalDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Saving Goal", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Goal name field
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Goal Name:"), BorderLayout.NORTH);
        JTextField nameField = new JTextField();
        namePanel.add(nameField, BorderLayout.CENTER);
        contentPanel.add(namePanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // Target amount field
        JPanel amountPanel = new JPanel(new BorderLayout());
        amountPanel.add(new JLabel("Target Amount ($):"), BorderLayout.NORTH);
        
        NumberFormat format = DecimalFormat.getInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.01);
        formatter.setAllowsInvalid(false);
        
        JFormattedTextField amountField = new JFormattedTextField(formatter);
        amountField.setValue(100.00);
        amountPanel.add(amountField, BorderLayout.CENTER);
        contentPanel.add(amountPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // End date field
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(new JLabel("End Date (YYYY-MM-DD):"), BorderLayout.NORTH);
        JTextField dateField = new JTextField(LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
        datePanel.add(dateField, BorderLayout.CENTER);
        contentPanel.add(datePanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // Reminder frequency dropdown
        JPanel frequencyPanel = new JPanel(new BorderLayout());
        frequencyPanel.add(new JLabel("Reminder Frequency:"), BorderLayout.NORTH);
        JComboBox<String> frequencyCombo = new JComboBox<>(new String[]{"DAILY", "WEEKLY", "MONTHLY"});
        frequencyCombo.setSelectedItem("WEEKLY");
        frequencyPanel.add(frequencyCombo, BorderLayout.CENTER);
        contentPanel.add(frequencyPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonsPanel.add(cancelButton);
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(SUCCESS_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            try {
                String goalName = nameField.getText().trim();
                if (goalName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a goal name.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double targetAmount = ((Number) amountField.getValue()).doubleValue();
                
                LocalDate endDate = LocalDate.parse(dateField.getText().trim());
                if (endDate.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(dialog, "End date must be in the future.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                UserGoal.ReminderFrequency frequency = UserGoal.ReminderFrequency.valueOf((String) frequencyCombo.getSelectedItem());
                
                int goalId = goalController.createSavingGoal(goalName, targetAmount, endDate, frequency);
                if (goalId != -1) {
                    dialog.dispose();
                    refreshGoals();
                    JOptionPane.showMessageDialog(dialog, "Saving goal created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create saving goal.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonsPanel.add(saveButton);
        
        contentPanel.add(buttonsPanel);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Shows dialog for adding a new spending goal
     */
    private void showAddSpendingGoalDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Spending Budget", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Budget name field
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Budget Name:"), BorderLayout.NORTH);
        JTextField nameField = new JTextField();
        namePanel.add(nameField, BorderLayout.CENTER);
        contentPanel.add(namePanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // Budget amount field
        JPanel amountPanel = new JPanel(new BorderLayout());
        amountPanel.add(new JLabel("Budget Limit ($):"), BorderLayout.NORTH);
        
        NumberFormat format = DecimalFormat.getInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.01);
        formatter.setAllowsInvalid(false);
        
        JFormattedTextField amountField = new JFormattedTextField(formatter);
        amountField.setValue(500.00);
        amountPanel.add(amountField, BorderLayout.CENTER);
        contentPanel.add(amountPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // End date field
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(new JLabel("End Date (YYYY-MM-DD):"), BorderLayout.NORTH);
        JTextField dateField = new JTextField(LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
        datePanel.add(dateField, BorderLayout.CENTER);
        contentPanel.add(datePanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // Reminder frequency dropdown
        JPanel frequencyPanel = new JPanel(new BorderLayout());
        frequencyPanel.add(new JLabel("Reminder Frequency:"), BorderLayout.NORTH);
        JComboBox<String> frequencyCombo = new JComboBox<>(new String[]{"DAILY", "WEEKLY", "MONTHLY"});
        frequencyCombo.setSelectedItem("WEEKLY");
        frequencyPanel.add(frequencyCombo, BorderLayout.CENTER);
        contentPanel.add(frequencyPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonsPanel.add(cancelButton);
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(WARNING_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            try {
                String goalName = nameField.getText().trim();
                if (goalName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a budget name.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double budgetAmount = ((Number) amountField.getValue()).doubleValue();
                
                LocalDate endDate = LocalDate.parse(dateField.getText().trim());
                if (endDate.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(dialog, "End date must be in the future.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                UserGoal.ReminderFrequency frequency = UserGoal.ReminderFrequency.valueOf((String) frequencyCombo.getSelectedItem());
                
                int goalId = goalController.createSpendingGoal(goalName, budgetAmount, endDate, frequency);
                if (goalId != -1) {
                    dialog.dispose();
                    refreshGoals();
                    JOptionPane.showMessageDialog(dialog, "Spending budget created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create spending budget.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonsPanel.add(saveButton);
        
        contentPanel.add(buttonsPanel);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Refreshes the goals data in the tables
     */
    public void refreshGoals() {
        List<UserGoal> goals = goalController.getAllGoals();
        
        // Clear existing data
        savingGoalsModel.setRowCount(0);
        spendingGoalsModel.setRowCount(0);
        
        // Process each goal
        for (UserGoal goal : goals) {
            if (goal.getGoalType() == UserGoal.GoalType.SAVING) {
                // Add to saving goals table
                Object[] rowData = new Object[6];
                rowData[0] = goal.getGoalName();
                rowData[1] = String.format("%.2f", goal.getTargetAmount());
                rowData[2] = String.format("%.2f", goal.getCurrentAmount());
                
                // Progress status
                double progress = goal.getProgressPercentage();
                
                if (goal.isCompleted()) {
                    rowData[3] = "Completed!";
                } else if (progress >= 90) {
                    rowData[3] = "Almost there! (" + String.format("%.1f%%", progress) + ")";
                } else if (progress >= 50) {
                    rowData[3] = "Good progress (" + String.format("%.1f%%", progress) + ")";
                } else if (progress >= 25) {
                    rowData[3] = "Getting there (" + String.format("%.1f%%", progress) + ")";
                } else {
                    rowData[3] = "Just started (" + String.format("%.1f%%", progress) + ")";
                }
                
                // Days left
                long daysLeft = goal.getDaysLeft();
                
                if (daysLeft < 0) {
                    rowData[4] = "Expired";
                } else if (daysLeft == 0) {
                    rowData[4] = "Today";
                } else {
                    rowData[4] = daysLeft + " day" + (daysLeft > 1 ? "s" : "");
                }
                
                // Action buttons
                rowData[5] = goal.getId();
                
                savingGoalsModel.addRow(rowData);
            } else {
                // Add to spending goals table
                Object[] rowData = new Object[6];
                rowData[0] = goal.getGoalName();
                rowData[1] = String.format("%.2f", goal.getTargetAmount());
                rowData[2] = String.format("%.2f", goal.getCurrentAmount());
                
                // Budget status
                double remaining = goal.getTargetAmount() - goal.getCurrentAmount();
                double percentRemaining = (remaining / goal.getTargetAmount()) * 100;
                
                if (goal.isCompleted() && remaining >= 0) {
                    rowData[3] = "Good job! Under budget";
                } else if (remaining < 0) {
                    rowData[3] = "Exceeded by " + String.format("%.2f", -remaining);
                } else if (percentRemaining <= 10) {
                    rowData[3] = "Warning! Almost at limit";
                } else if (percentRemaining <= 25) {
                    rowData[3] = "Close to limit (" + String.format("%.1f%%", percentRemaining) + " left)";
                } else {
                    rowData[3] = "On track (" + String.format("%.1f%%", percentRemaining) + " left)";
                }
                
                // Days left
                long daysLeft = goal.getDaysLeft();
                
                if (daysLeft < 0) {
                    rowData[4] = "Expired";
                } else if (daysLeft == 0) {
                    rowData[4] = "Today";
                } else {
                    rowData[4] = daysLeft + " day" + (daysLeft > 1 ? "s" : "");
                }
                
                // Action buttons
                rowData[5] = goal.getId();
                
                spendingGoalsModel.addRow(rowData);
            }
        }
        
        // Check if reminders need to be sent
        goalController.processReminders();
    }
    
    /**
     * Shows dialog for updating a goal's amount
     * @param goalId the goal ID
     * @param isSavingGoal whether it's a saving goal or spending goal
     */
    private void showUpdateAmountDialog(int goalId, boolean isSavingGoal) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Update " + (isSavingGoal ? "Saving" : "Spending") + " Amount", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Amount field
        JPanel amountPanel = new JPanel(new BorderLayout());
        amountPanel.add(new JLabel("Current " + (isSavingGoal ? "Saved" : "Spent") + " Amount ($):"), BorderLayout.NORTH);
        
        NumberFormat format = DecimalFormat.getInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);
        formatter.setAllowsInvalid(false);
        
        JFormattedTextField amountField = new JFormattedTextField(formatter);
        amountField.setValue(0.00);
        amountPanel.add(amountField, BorderLayout.CENTER);
        contentPanel.add(amountPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonsPanel.add(cancelButton);
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(PRIMARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            try {
                double newAmount = ((Number) amountField.getValue()).doubleValue();
                
                boolean updated = goalController.updateGoalAmount(goalId, newAmount);
                if (updated) {
                    dialog.dispose();
                    refreshGoals();
                    JOptionPane.showMessageDialog(dialog, 
                            (isSavingGoal ? "Saving" : "Spending") + " amount updated successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                            "Failed to update " + (isSavingGoal ? "saving" : "spending") + " amount.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonsPanel.add(saveButton);
        
        contentPanel.add(buttonsPanel);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Deletes a goal after confirmation
     * @param goalId the goal ID
     */
    private void deleteGoal(int goalId) {
        int response = JOptionPane.showConfirmDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Are you sure you want to delete this goal?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            boolean deleted = goalController.deleteGoal(goalId);
            if (deleted) {
                refreshGoals();
                JOptionPane.showMessageDialog((Frame) SwingUtilities.getWindowAncestor(this),
                        "Goal deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog((Frame) SwingUtilities.getWindowAncestor(this),
                        "Failed to delete goal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Custom renderer for buttons in tables
     */
    private class ButtonRenderer implements TableCellRenderer {
        private JPanel panel;
        private JButton updateButton;
        private JButton deleteButton;
        
        public ButtonRenderer() {
            panel = new JPanel(new GridLayout(1, 2, 5, 0));
            
            updateButton = new JButton("Update");
            deleteButton = new JButton("Delete");
            
            updateButton.setBackground(PRIMARY_COLOR);
            updateButton.setForeground(Color.WHITE);
            updateButton.setFocusPainted(false);
            
            deleteButton.setBackground(DANGER_COLOR);
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            
            panel.add(updateButton);
            panel.add(deleteButton);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            return panel;
        }
    }
    
    /**
     * Custom editor for buttons in tables
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JPanel panel = new JPanel();
        private JButton updateButton = new JButton("Update");
        private JButton deleteButton = new JButton("Delete");
        private int goalId;
        private boolean isSavingGoal;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel.setLayout(new GridLayout(1, 2, 5, 0));
            panel.add(updateButton);
            panel.add(deleteButton);
            
            updateButton.setBackground(PRIMARY_COLOR);
            updateButton.setForeground(Color.WHITE);
            updateButton.setFocusPainted(false);
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    showUpdateAmountDialog(goalId, isSavingGoal);
                }
            });
            
            deleteButton.setBackground(DANGER_COLOR);
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    deleteGoal(goalId);
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            goalId = (int) value;
            isSavingGoal = table == savingGoalsTable;
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return goalId;
        }
    }
} 