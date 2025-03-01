package main.java.view;

import main.java.controller.TransactionController;
import main.java.controller.UserController;
import main.java.model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionLogPanel extends JPanel {
    
    private UserController userController;
    private TransactionController transactionController;
    private boolean isDatabaseConnected;
    
    // UI Components
    private JTextField amountField;
    private JTextField descriptionField;
    private JRadioButton expenseButton;
    private JRadioButton incomeButton;
    private JComboBox<String> categoryComboBox;
    private JLabel statusLabel;
    
    // Date components
    private JSpinner daySpinner;
    private JComboBox<String> monthComboBox;
    private JSpinner yearSpinner;
    
    // Listener for transaction events
    private List<TransactionListener> listeners = new ArrayList<>();
    
    public TransactionLogPanel(UserController userController, 
                              TransactionController transactionController, 
                              boolean isDatabaseConnected) {
        this.userController = userController;
        this.transactionController = transactionController;
        this.isDatabaseConnected = isDatabaseConnected;
        
        initializeUI();
    }
    
    private void initializeUI() {
        setBackground(MainUI.MEDIUM_PURPLE);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Log Your Quest Rewards");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JLabel subtitleLabel = new JLabel("Track your income and expenses to gain experience");
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        // Transaction type
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setOpaque(false);
        
        JLabel typeLabel = new JLabel("Transaction Type");
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        expenseButton = new JRadioButton("Expense");
        expenseButton.setForeground(new Color(255, 100, 100));
        expenseButton.setBackground(null);
        expenseButton.setOpaque(false);
        expenseButton.setSelected(true);
        
        incomeButton = new JRadioButton("Income");
        incomeButton.setForeground(new Color(100, 255, 100));
        incomeButton.setBackground(null);
        incomeButton.setOpaque(false);
        
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(expenseButton);
        typeGroup.add(incomeButton);
        
        typePanel.add(typeLabel);
        typePanel.add(expenseButton);
        typePanel.add(incomeButton);
        
        // Amount field
        JPanel amountPanel = new JPanel(new BorderLayout());
        amountPanel.setOpaque(false);
        amountPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel amountLabel = new JLabel("Amount");
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        amountField = new JTextField();
        styleTextField(amountField);
        
        amountPanel.add(amountLabel, BorderLayout.NORTH);
        amountPanel.add(amountField, BorderLayout.CENTER);
        
        // Category dropdown
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setOpaque(false);
        categoryPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setForeground(Color.WHITE);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        categoryComboBox = new JComboBox<>(new String[] {
            "Food & Dining", "Shopping", "Utilities", "Housing", 
            "Entertainment", "Coffee", "Gifts", "Transportation", 
            "Education", "Emergency Fund", "Credit Card Debt",
            "Stock Portfolio", "Home Down Payment", "Other"
        });
        styleComboBox(categoryComboBox);
        
        categoryPanel.add(categoryLabel, BorderLayout.NORTH);
        categoryPanel.add(categoryComboBox, BorderLayout.CENTER);
        
        // Date panel
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setOpaque(false);
        datePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel dateLabel = new JLabel("Date");
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JPanel datePickerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePickerPanel.setOpaque(false);
        
        // Day spinner
        SpinnerNumberModel dayModel = new SpinnerNumberModel(
                LocalDate.now().getDayOfMonth(), 1, 31, 1);
        daySpinner = new JSpinner(dayModel);
        styleSpinner(daySpinner);
        
        // Month combo
        String[] months = {"January", "February", "March", "April", "May", "June", 
                           "July", "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        styleComboBox(monthComboBox);
        
        // Year spinner
        SpinnerNumberModel yearModel = new SpinnerNumberModel(
                LocalDate.now().getYear(), 2000, 2100, 1);
        yearSpinner = new JSpinner(yearModel);
        styleSpinner(yearSpinner);
        
        datePickerPanel.add(monthComboBox);
        datePickerPanel.add(daySpinner);
        datePickerPanel.add(yearSpinner);
        
        datePanel.add(dateLabel, BorderLayout.NORTH);
        datePanel.add(datePickerPanel, BorderLayout.CENTER);
        
        // Description field
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setOpaque(false);
        descPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel descLabel = new JLabel("Description");
        descLabel.setForeground(Color.WHITE);
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        descriptionField = new JTextField();
        styleTextField(descriptionField);
        
        descPanel.add(descLabel, BorderLayout.NORTH);
        descPanel.add(descriptionField, BorderLayout.CENTER);
        
        // Status label
        statusLabel = new JLabel();
        statusLabel.setForeground(isDatabaseConnected ? 
                new Color(100, 255, 100) : new Color(255, 100, 100));
        statusLabel.setText(isDatabaseConnected ? 
                "Connected to database" : "Offline mode - transactions won't be saved");
        
        // Submit button
        JButton submitButton = new JButton("Log Transaction (+XP)");
        submitButton.setBackground(MainUI.LIGHT_PURPLE);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTransaction();
            }
        });
        
        // Add all components to form panel
        formPanel.add(typePanel);
        formPanel.add(amountPanel);
        formPanel.add(categoryPanel);
        formPanel.add(datePanel);
        formPanel.add(descPanel);
        formPanel.add(statusLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(submitButton);
        
        // Add panels to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }
    
    private void addTransaction() {
        try {
            // Get values from form
            String description = descriptionField.getText().trim();
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                        "Please enter a description for your transaction.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                        "Please enter a valid positive amount.",
                        "Invalid Amount", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            boolean isIncome = incomeButton.isSelected();
            String category = (String) categoryComboBox.getSelectedItem();
            
            // Get date
            int day = (int) daySpinner.getValue();
            int month = monthComboBox.getSelectedIndex() + 1;
            int year = (int) yearSpinner.getValue();
            
            LocalDate date;
            try {
                date = LocalDate.of(year, month, day);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Please enter a valid date.",
                        "Invalid Date", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Create transaction
            Transaction transaction;
            
            if (isDatabaseConnected) {
                transaction = transactionController.addTransaction(
                        description, amount, date, isIncome, category);
                
                if (transaction == null) {
                    JOptionPane.showMessageDialog(this, 
                            "Failed to add transaction to database.",
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Create local transaction
                transaction = new Transaction(description, amount, date, isIncome, category);
            }
            
            // Notify listeners
            for (TransactionListener listener : listeners) {
                listener.onTransactionAdded(transaction);
            }
            
            // Clear form fields
            descriptionField.setText("");
            amountField.setText("");
            
            // Show confirmation message
            JOptionPane.showMessageDialog(this, 
                    "Transaction recorded successfully!\n" +
                    "You gained " + calculateXP(amount) + " XP!",
                    "Quest Complete", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                    "An error occurred: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int calculateXP(double amount) {
        // Simple XP calculation
        return (int)(amount * 10);
    }
    
    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBackground(new Color(48, 30, 75));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 40, 110)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
    }
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(new Color(48, 30, 75));
        comboBox.setForeground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 40, 110)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Arial", Font.PLAIN, 14));
        spinner.getEditor().getComponent(0).setBackground(new Color(48, 30, 75));
        spinner.getEditor().getComponent(0).setForeground(Color.WHITE);
        spinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 40, 110)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }
    
    public void addTransactionListener(TransactionListener listener) {
        listeners.add(listener);
    }
    
    public interface TransactionListener {
        void onTransactionAdded(Transaction transaction);
    }
} 