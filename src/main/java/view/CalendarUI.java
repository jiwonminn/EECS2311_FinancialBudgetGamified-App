package view;

import controller.TransactionController;
import controller.UserController;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

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
    private DatePicker datePicker;
    private JTextField descriptionField;
    private JTextField amountField;
    private JRadioButton incomeButton;
    private JRadioButton expenseButton;
    private JTextArea transactionDisplay;
    private JComboBox<String> categoryComboBox;
    private String userName;
    private String userEmail;

    public CalendarUI(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
        
        // Initialize the UserController with user details
        UserController userController = new UserController(userName, userEmail, 1000);

        transactionController = new TransactionController();
        setTitle("Financial Budget Gamified - Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Use a BorderLayout for the main frame
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create header panel with user info
        JPanel headerPanel = createHeaderPanel(userName);
        add(headerPanel, BorderLayout.NORTH);
        
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
        
        setVisible(true);
    }
    
    // Add default constructor for backward compatibility
    public CalendarUI() {
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
        setTitle("Financial Budget Gamified - Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Use a BorderLayout for the main frame
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create header panel with user info
        JPanel headerPanel = createHeaderPanel(userName);
        add(headerPanel, BorderLayout.NORTH);
        
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
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel(String userName) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Level 5 Budget Warrior");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
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
    
    private JPanel createTransactionLogPanel() {
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        logPanel.setBackground(PANEL_COLOR);
        logPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Log Your Quest Rewards");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(titleLabel);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Track your income and expenses to gain experience");
        subtitleLabel.setForeground(new Color(180, 180, 180));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(subtitleLabel);
        logPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Transaction Type
        JLabel typeLabel = new JLabel("Transaction Type");
        typeLabel.setForeground(TEXT_COLOR);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(typeLabel);
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.setBackground(PANEL_COLOR);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        expenseButton = new JRadioButton("Expense");
        expenseButton.setForeground(EXPENSE_COLOR);
        expenseButton.setBackground(PANEL_COLOR);
        expenseButton.setSelected(true);
        
        incomeButton = new JRadioButton("Income");
        incomeButton.setForeground(INCOME_COLOR);
        incomeButton.setBackground(PANEL_COLOR);
        
        ButtonGroup group = new ButtonGroup();
        group.add(expenseButton);
        group.add(incomeButton);
        
        radioPanel.add(expenseButton);
        radioPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        radioPanel.add(incomeButton);
        logPanel.add(radioPanel);
        logPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Amount
        JLabel amountLabel = new JLabel("Amount");
        amountLabel.setForeground(TEXT_COLOR);
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(amountLabel);
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        amountField = new JTextField();
        amountField.setBackground(FIELD_BACKGROUND);
        amountField.setForeground(TEXT_COLOR);
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logPanel.add(amountField);
        logPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Category (Dropdown)
        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setForeground(TEXT_COLOR);
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(categoryLabel);
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        String[] categories = {
            "Food & Dining", "Shopping", "Utilities", "Housing", 
            "Entertainment", "Coffee", "Gifts", "Emergency Fund",
            "Transport", "Education", "Health", "Other"
        };
        
        categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setBackground(FIELD_BACKGROUND);
        categoryComboBox.setForeground(TEXT_COLOR);
        categoryComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        categoryComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logPanel.add(categoryComboBox);
        logPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Date
        JLabel dateLabel = new JLabel("Date");
        dateLabel.setForeground(TEXT_COLOR);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(dateLabel);
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Custom Date Picker with a dropdown calendar
        datePicker = new DatePicker();
        datePicker.setBackground(FIELD_BACKGROUND);
        datePicker.setForeground(TEXT_COLOR);
        datePicker.setBorder(BorderFactory.createLineBorder(FIELD_BORDER));
        datePicker.setAlignmentX(Component.LEFT_ALIGNMENT);
        datePicker.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logPanel.add(datePicker);
        logPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Description
        JLabel descLabel = new JLabel("Description");
        descLabel.setForeground(TEXT_COLOR);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logPanel.add(descLabel);
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        descriptionField = new JTextField();
        descriptionField.setBackground(FIELD_BACKGROUND);
        descriptionField.setForeground(TEXT_COLOR);
        descriptionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        descriptionField.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logPanel.add(descriptionField);
        logPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Submit Button
        JButton submitButton = new JButton("Log Transaction");
        submitButton.setBackground(ACCENT_COLOR);
        submitButton.setForeground(TEXT_COLOR);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logTransaction();
            }
        });
        
        logPanel.add(submitButton);
        
        return logPanel;
    }
    
    private JPanel createTransactionHistoryPanel() {
        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setBackground(PANEL_COLOR);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Recent Transactions");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        historyPanel.add(titleLabel);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Your latest financial activities");
        subtitleLabel.setForeground(new Color(180, 180, 180));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        historyPanel.add(subtitleLabel);
        historyPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Transaction list
        transactionDisplay = new JTextArea(10, 30);
        transactionDisplay.setEditable(false);
        transactionDisplay.setBackground(PANEL_COLOR);
        transactionDisplay.setForeground(TEXT_COLOR);
        transactionDisplay.setLineWrap(true);
        transactionDisplay.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(transactionDisplay);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(PANEL_COLOR);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        historyPanel.add(scrollPane);
        
        return historyPanel;
    }

    private void logTransaction() {
        try {
            String description = descriptionField.getText();
            if (description.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a description", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String amountText = amountField.getText();
            if (amountText.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Date selectedDate = datePicker.getDate();
            LocalDate date = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            boolean isIncome = incomeButton.isSelected();
            String category = (String) categoryComboBox.getSelectedItem();
            
            // Add the transaction
            transactionController.addTransaction(description, amount, date, isIncome);
            
            // Update display
            updateTransactionDisplay();
            
            // Clear fields
            descriptionField.setText("");
            amountField.setText("");
            
            // Show success message
            JOptionPane.showMessageDialog(this, 
                "Transaction logged successfully!\nYou earned 10 XP!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for amount", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error logging transaction: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTransactionDisplay() {
        transactionDisplay.setText("");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        for (Transaction t : transactionController.getTransactions()) {
            String dateStr = sdf.format(Date.from(t.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            String type = t.isIncome() ? "Income" : "Expense";
            String amount = String.format("$%.2f", t.getAmount());
            
            StringBuilder sb = new StringBuilder();
            sb.append(t.getDescription()).append("\n");
            sb.append(dateStr).append(" • ").append(type).append("\n");
            sb.append(amount).append("\n\n");
            
            transactionDisplay.append(sb.toString());
        }
    }

    /**
     * Custom DatePicker component with a dropdown calendar
     */
    private class DatePicker extends JPanel {
        private JTextField dateField;
        private JButton calendarButton;
        private JDialog calendarDialog;
        private Calendar selectedDate;
        private SimpleDateFormat dateFormat;
        
        public DatePicker() {
            setLayout(new BorderLayout());
            
            // Initialize date and formatter
            selectedDate = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            
            // Create text field to display the date
            dateField = new JTextField();
            dateField.setEditable(false);
            dateField.setText(dateFormat.format(selectedDate.getTime()));
            dateField.setBackground(FIELD_BACKGROUND);
            dateField.setForeground(TEXT_COLOR);
            dateField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            add(dateField, BorderLayout.CENTER);
            
            // Create button to show calendar
            calendarButton = new JButton("\u25BC"); // Down triangle symbol
            calendarButton.setBackground(FIELD_BACKGROUND);
            calendarButton.setForeground(TEXT_COLOR);
            calendarButton.setFocusPainted(false);
            calendarButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            calendarButton.addActionListener(e -> showCalendarDialog());
            add(calendarButton, BorderLayout.EAST);
        }
        
        public Date getDate() {
            return selectedDate.getTime();
        }
        
        private void showCalendarDialog() {
            if (calendarDialog != null && calendarDialog.isVisible()) {
                return;
            }
            
            // Create dialog
            calendarDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Date", true);
            calendarDialog.setLayout(new BorderLayout());
            calendarDialog.setSize(300, 350);
            calendarDialog.setLocationRelativeTo(this);
            
            // Main calendar panel (purple background)
            JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
            mainPanel.setBackground(PANEL_COLOR);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Month and year header panel
            JPanel headerPanel = new JPanel(new BorderLayout(5, 0));
            headerPanel.setBackground(PANEL_COLOR);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 5));
            
            // Previous month button (left arrow in white box)
            JButton prevButton = new JButton("<");
            styleNavigationButton(prevButton);
            prevButton.addActionListener(e -> {
                selectedDate.add(Calendar.MONTH, -1);
                updateCalendarDialog();
            });
            
            // Next month button (right arrow in white box)
            JButton nextButton = new JButton(">");
            styleNavigationButton(nextButton);
            nextButton.addActionListener(e -> {
                selectedDate.add(Calendar.MONTH, 1);
                updateCalendarDialog();
            });
            
            // Create center panel for month and year selection
            JPanel monthYearPanel = new JPanel();
            monthYearPanel.setLayout(new BoxLayout(monthYearPanel, BoxLayout.X_AXIS));
            monthYearPanel.setBackground(PANEL_COLOR);
            
            // Create month dropdown
            String[] months = {"January", "February", "March", "April", "May", "June", 
                              "July", "August", "September", "October", "November", "December"};
            JComboBox<String> monthComboBox = new JComboBox<>(months);
            monthComboBox.setSelectedIndex(selectedDate.get(Calendar.MONTH));
            monthComboBox.setBackground(Color.WHITE);
            monthComboBox.setForeground(Color.BLACK);
            monthComboBox.addActionListener(e -> {
                selectedDate.set(Calendar.MONTH, monthComboBox.getSelectedIndex());
                updateCalendarDialog();
            });
            
            // Create year dropdown (current year +/- 50 years)
            int currentYear = selectedDate.get(Calendar.YEAR);
            Integer[] years = new Integer[101];
            for (int i = 0; i < years.length; i++) {
                years[i] = currentYear - 50 + i;
            }
            JComboBox<Integer> yearComboBox = new JComboBox<>(years);
            yearComboBox.setSelectedItem(currentYear);
            yearComboBox.setBackground(Color.WHITE);
            yearComboBox.setForeground(Color.BLACK);
            yearComboBox.addActionListener(e -> {
                selectedDate.set(Calendar.YEAR, (Integer) yearComboBox.getSelectedItem());
                updateCalendarDialog();
            });
            
            // Add dropdowns to panel with some spacing
            monthYearPanel.add(monthComboBox);
            monthYearPanel.add(Box.createRigidArea(new Dimension(5, 0)));
            monthYearPanel.add(yearComboBox);
            
            headerPanel.add(prevButton, BorderLayout.WEST);
            headerPanel.add(monthYearPanel, BorderLayout.CENTER);
            headerPanel.add(nextButton, BorderLayout.EAST);
            
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            
            // Days of week panel
            JPanel daysPanel = new JPanel(new GridLayout(1, 7, 0, 0));
            daysPanel.setBackground(PANEL_COLOR);
            
            String[] dayNames = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
            for (String day : dayNames) {
                JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
                dayLabel.setForeground(TEXT_COLOR);
                dayLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                daysPanel.add(dayLabel);
            }
            
            mainPanel.add(daysPanel, BorderLayout.CENTER);
            
            // Calendar days grid
            JPanel datesPanel = new JPanel(new GridLayout(6, 7, 3, 3));
            datesPanel.setBackground(PANEL_COLOR);
            
            mainPanel.add(datesPanel, BorderLayout.SOUTH);
            
            // Bottom action buttons panel
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            bottomPanel.setBackground(PANEL_COLOR);
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            
            // Today button
            JButton todayButton = new JButton("Today");
            styleActionButton(todayButton);
            todayButton.addActionListener(e -> {
                selectedDate = Calendar.getInstance();
                updateDateField();
                calendarDialog.dispose();
            });
            
            // Cancel button
            JButton cancelButton = new JButton("Cancel");
            styleActionButton(cancelButton);
            cancelButton.addActionListener(e -> calendarDialog.dispose());
            
            bottomPanel.add(todayButton);
            bottomPanel.add(cancelButton);
            
            // Add all panels to dialog
            calendarDialog.add(mainPanel, BorderLayout.CENTER);
            calendarDialog.add(bottomPanel, BorderLayout.SOUTH);
            
            // Fill days when the dialog is shown
            updateCalendarDialog(datesPanel);
            
            calendarDialog.setVisible(true);
        }
        
        private void styleNavigationButton(JButton button) {
            button.setPreferredSize(new Dimension(30, 30));
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorderPainted(true);
            button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }
        
        private void styleActionButton(JButton button) {
            button.setPreferredSize(new Dimension(80, 30));
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorderPainted(true);
            button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }
        
        private void styleDayButton(JButton button, boolean isSelectedDay, boolean isToday) {
            button.setPreferredSize(new Dimension(30, 25));
            button.setBackground(isToday ? new Color(230, 230, 250) : Color.WHITE); // Light lavender for today
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorderPainted(true);
            
            if (isSelectedDay) {
                button.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
            } else {
                button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            }
        }
        
        private void updateCalendarDialog(JPanel datesPanel) {
            if (datesPanel != null) {
                // Clear existing days
                datesPanel.removeAll();
                
                // Get calendar for the selected month
                Calendar cal = (Calendar) selectedDate.clone();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                
                // Determine the day of week for the first day of month
                int firstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK);
                
                // Add empty buttons for days before the first day of month
                for (int i = 1; i < firstDayOfMonth; i++) {
                    JButton emptyButton = new JButton();
                    emptyButton.setEnabled(false);
                    emptyButton.setOpaque(false);
                    emptyButton.setContentAreaFilled(false);
                    emptyButton.setBorderPainted(false);
                    datesPanel.add(emptyButton);
                }
                
                // Determine how many days in month - properly calculate for each month
                int daysInMonth = 0;
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                
                switch (month) {
                    case Calendar.JANUARY:
                    case Calendar.MARCH:
                    case Calendar.MAY:
                    case Calendar.JULY:
                    case Calendar.AUGUST:
                    case Calendar.OCTOBER:
                    case Calendar.DECEMBER:
                        daysInMonth = 31;
                        break;
                    case Calendar.APRIL:
                    case Calendar.JUNE:
                    case Calendar.SEPTEMBER:
                    case Calendar.NOVEMBER:
                        daysInMonth = 30;
                        break;
                    case Calendar.FEBRUARY:
                        // Check for leap year
                        boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                        daysInMonth = isLeapYear ? 29 : 28;
                        break;
                }
                
                // Get today for highlighting
                Calendar today = Calendar.getInstance();
                
                // Add buttons for each day of the month
                for (int day = 1; day <= daysInMonth; day++) {
                    final int selectedDay = day;
                    JButton dayButton = new JButton(String.valueOf(day));
                    
                    boolean isToday = cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                     cal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                                     day == today.get(Calendar.DAY_OF_MONTH);
                                     
                    boolean isSelectedDay = cal.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                                          cal.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                                          day == selectedDate.get(Calendar.DAY_OF_MONTH);
                    
                    styleDayButton(dayButton, isSelectedDay, isToday);
                    
                    dayButton.addActionListener(e -> {
                        selectedDate.set(Calendar.DAY_OF_MONTH, selectedDay);
                        updateDateField();
                        calendarDialog.dispose();
                    });
                    
                    datesPanel.add(dayButton);
                }
                
                // Add empty buttons for remaining cells in the grid (if needed)
                int remainingCells = 42 - (firstDayOfMonth - 1) - daysInMonth;
                for (int i = 0; i < remainingCells; i++) {
                    JButton emptyButton = new JButton();
                    emptyButton.setEnabled(false);
                    emptyButton.setOpaque(false);
                    emptyButton.setContentAreaFilled(false);
                    emptyButton.setBorderPainted(false);
                    datesPanel.add(emptyButton);
                }
                
                datesPanel.revalidate();
                datesPanel.repaint();
                
                // Update month and year dropdowns if they exist
                Container container = calendarDialog.getContentPane();
                if (container.getComponentCount() > 0) {
                    Component comp = container.getComponent(0);
                    if (comp instanceof JPanel) {
                        JPanel mainPanel = (JPanel) comp;
                        if (mainPanel.getComponentCount() > 0) {
                            Component headerComp = mainPanel.getComponent(0);
                            if (headerComp instanceof JPanel) {
                                JPanel headerPanel = (JPanel) headerComp;
                                Component centerComp = ((BorderLayout)headerPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                                if (centerComp instanceof JPanel) {
                                    JPanel monthYearPanel = (JPanel) centerComp;
                                    Component[] monthYearComps = monthYearPanel.getComponents();
                                    for (Component c : monthYearComps) {
                                        if (c instanceof JComboBox) {
                                            JComboBox<?> comboBox = (JComboBox<?>) c;
                                            if (comboBox.getItemCount() == 12) {
                                                // Month combo box
                                                comboBox.setSelectedIndex(month);
                                            } else if (comboBox.getItemCount() > 12) {
                                                // Year combo box
                                                for (int i = 0; i < comboBox.getItemCount(); i++) {
                                                    if (comboBox.getItemAt(i).equals(year)) {
                                                        comboBox.setSelectedIndex(i);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        private void updateDateField() {
            dateField.setText(dateFormat.format(selectedDate.getTime()));
        }

        private void updateCalendarDialog() {
            if (calendarDialog != null && calendarDialog.isVisible()) {
                Component component = calendarDialog.getContentPane().getComponent(0);
                if (component instanceof JPanel) {
                    JPanel mainPanel = (JPanel) component;
                    if (mainPanel.getComponentCount() > 2) {
                        Component datesComponent = mainPanel.getComponent(2);
                        if (datesComponent instanceof JPanel) {
                            updateCalendarDialog((JPanel) datesComponent);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Add custom styling to UI defaults
            UIManager.put("OptionPane.background", new Color(40, 24, 69));
            UIManager.put("Panel.background", new Color(40, 24, 69));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch login screen directly instead of CalendarUI
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}
