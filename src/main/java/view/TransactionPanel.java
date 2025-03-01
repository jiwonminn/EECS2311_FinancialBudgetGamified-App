package main.java.view;

import main.java.controller.TransactionController;
import main.java.controller.UserController;
import main.java.model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionPanel extends JPanel {
    
    private UserController userController;
    private TransactionController transactionController;
    private DefaultTableModel tableModel;
    private JTable transactionTable;
    private boolean isDatabaseConnected;
    
    public TransactionPanel(UserController userController) {
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
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel with title and filters
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Transaction table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Load transactions
        loadTransactions();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainUI.MEDIUM_PURPLE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        
        JLabel subtitleLabel = new JLabel("View and manage your financial activities");
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);
        
        JComboBox<String> categoryFilter = new JComboBox<>(new String[] {
            "All Categories", "Food & Dining", "Shopping", "Utilities", "Housing", 
            "Entertainment", "Coffee", "Gifts", "Transportation", "Education"
        });
        styleComboBox(categoryFilter);
        
        JComboBox<String> timeFilter = new JComboBox<>(new String[] {
            "All Time", "This Month", "Last Month", "This Year"
        });
        styleComboBox(timeFilter);
        
        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTransactions();
            }
        });
        
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilter);
        filterPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        filterPanel.add(new JLabel("Time:"));
        filterPanel.add(timeFilter);
        filterPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        filterPanel.add(refreshButton);
        
        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainUI.DARK_PURPLE);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Create table model
        String[] columnNames = {"Date", "Description", "Category", "Amount", "Type"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        transactionTable = new JTable(tableModel);
        
        // Style the table
        transactionTable.setBackground(MainUI.MEDIUM_PURPLE);
        transactionTable.setForeground(Color.WHITE);
        transactionTable.setGridColor(new Color(60, 40, 80));
        transactionTable.getTableHeader().setBackground(MainUI.MEDIUM_PURPLE);
        transactionTable.getTableHeader().setForeground(Color.WHITE);
        transactionTable.setRowHeight(30);
        transactionTable.setFillsViewportHeight(true);
        
        // Custom cell renderer for amount column
        transactionTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String type = (String) table.getValueAt(row, 4);
                if (type.equals("Income")) {
                    c.setForeground(new Color(100, 255, 100));
                } else {
                    c.setForeground(new Color(255, 100, 100));
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.getViewport().setBackground(MainUI.MEDIUM_PURPLE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadTransactions() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        if (isDatabaseConnected) {
            List<Transaction> transactions = transactionController.getTransactions();
            
            if (transactions.isEmpty()) {
                // No transactions yet
                JOptionPane.showMessageDialog(this,
                        "You haven't recorded any transactions yet.\n" +
                        "Start your financial journey by logging transactions on the Dashboard.",
                        "No Transactions", JOptionPane.INFORMATION_MESSAGE);
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                
                for (Transaction t : transactions) {
                    String date = t.getDate().format(formatter);
                    String description = t.getDescription();
                    String category = t.getCategory();
                    String amount = String.format("$%.2f", t.getAmount());
                    String type = t.isIncome() ? "Income" : "Expense";
                    
                    tableModel.addRow(new Object[]{date, description, category, amount, type});
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Database connection is not available.\n" +
                    "Please check your database setup and try again.",
                    "Database Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(new Color(48, 30, 75));
        comboBox.setForeground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    
    private void styleButton(JButton button) {
        button.setBackground(MainUI.LIGHT_PURPLE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
} 