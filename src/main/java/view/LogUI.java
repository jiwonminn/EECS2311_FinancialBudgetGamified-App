package view;

import controller.TransactionController;
import model.Transaction;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class LogUI extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    private static final Color PANEL_COLOR = new Color(40, 24, 69);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color ACCENT_COLOR = new Color(128, 90, 213);
    private static final Color EXPENSE_COLOR = new Color(215, 38, 61);
    private static final Color INCOME_COLOR = new Color(39, 174, 96);
    
    private TransactionController transactionController;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterTypeComboBox;
    private JTextField searchField;
    private static LogUI instance;
    
    public LogUI() {
        try {
			transactionController = new TransactionController();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Initial data load
        refreshTransactionData();
        
        instance = this;
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 10));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Title
        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Filter controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlsPanel.setBackground(BACKGROUND_COLOR);
        
        // Transaction type filter
        filterTypeComboBox = new JComboBox<>(new String[]{"All", "Income", "Expense"});
        filterTypeComboBox.setBackground(PANEL_COLOR);
        filterTypeComboBox.setForeground(TEXT_COLOR);
        filterTypeComboBox.addActionListener(e -> refreshTransactionData());
        
        // Search field
        searchField = new JTextField(20);
        searchField.setBackground(PANEL_COLOR);
        searchField.setForeground(TEXT_COLOR);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 50, 110)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(ACCENT_COLOR);
        searchButton.setForeground(TEXT_COLOR);
        searchButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        searchButton.addActionListener(e -> refreshTransactionData());
        
        controlsPanel.add(new JLabel("Filter: ") {{ setForeground(TEXT_COLOR); }});
        controlsPanel.add(filterTypeComboBox);
        controlsPanel.add(new JLabel("Search: ") {{ setForeground(TEXT_COLOR); }});
        controlsPanel.add(searchField);
        controlsPanel.add(searchButton);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(controlsPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(PANEL_COLOR);
        tablePanel.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));
        
        // Create table model with columns
        String[] columns = {"Date", "Description", "Category", "Type", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create and configure the table
        transactionTable = new JTable(tableModel);
        transactionTable.setBackground(PANEL_COLOR);
        transactionTable.setForeground(TEXT_COLOR);
        transactionTable.setGridColor(new Color(70, 50, 110));
        transactionTable.getTableHeader().setBackground(new Color(30, 18, 50));
        transactionTable.getTableHeader().setForeground(TEXT_COLOR);
        transactionTable.setRowHeight(30);
        transactionTable.setShowGrid(true);
        
        // Center align all columns except Description
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(ACCENT_COLOR);
                } else {
                    c.setBackground(PANEL_COLOR);
                }
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Custom renderer for the Amount column
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String type = (String) table.getValueAt(row, 3); // Type column
                if (type.equals("Income")) {
                    setForeground(INCOME_COLOR);
                } else {
                    setForeground(EXPENSE_COLOR);
                }
                if (isSelected) {
                    c.setBackground(ACCENT_COLOR);
                } else {
                    c.setBackground(PANEL_COLOR);
                }
                return c;
            }
        };
        amountRenderer.setHorizontalAlignment(JLabel.RIGHT);
        
        // Apply renderers
        for (int i = 0; i < transactionTable.getColumnCount(); i++) {
            if (i != 1) { // Skip Description column
                transactionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        transactionTable.getColumnModel().getColumn(4).setCellRenderer(amountRenderer); // Amount column
        
        // Add sorting capability
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        transactionTable.setRowSorter(sorter);
        
        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBackground(PANEL_COLOR);
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Customize the scrollbar
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 80, 140);
                this.trackColor = new Color(24, 15, 41);
            }
        });
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }
    
    private void refreshTransactionData() {
        tableModel.setRowCount(0); // Clear existing data
        
        try {
            List<Transaction> transactions = transactionController.getTransactions();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String searchText = searchField.getText().toLowerCase();
            String filterType = (String) filterTypeComboBox.getSelectedItem();
            
            for (Transaction transaction : transactions) {
                // Apply filters
                boolean matchesSearch = searchText.isEmpty() ||
                    transaction.getDescription().toLowerCase().contains(searchText) ||
                    transaction.getCategory().toLowerCase().contains(searchText);
                
                boolean matchesFilter = filterType.equals("All") ||
                    (filterType.equals("Income") && transaction.isIncome()) ||
                    (filterType.equals("Expense") && !transaction.isIncome());
                
                if (matchesSearch && matchesFilter) {
                    // Format date
                    String date = dateFormat.format(Date.from(
                        transaction.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
                    ));
                    
                    // Format amount with currency symbol and 2 decimal places
                    String amount = String.format("$%.2f", Math.abs(transaction.getAmount()));
                    
                    // Add row to table
                    tableModel.addRow(new Object[]{
                        date,
                        transaction.getDescription(),
                        transaction.getCategory(),
                        transaction.isIncome() ? "Income" : "Expense",
                        amount
                    });
                }
            }
            
            // Show message if no transactions are displayed
            if (tableModel.getRowCount() == 0) {
                if (!searchText.isEmpty() || !filterType.equals("All")) {
                    showMessage("No transactions match the current filters.");
                } else {
                    showMessage("No transactions found. Add some transactions to get started!");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading transactions: " + e.getMessage());
        }
    }
    
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    public static LogUI getInstance() {
        if (instance == null) {
            instance = new LogUI();
        }
        return instance;
    }
} 