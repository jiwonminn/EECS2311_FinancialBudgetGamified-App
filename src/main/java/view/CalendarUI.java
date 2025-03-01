package view;

import controller.TransactionController;
import controller.UserController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.JSpinner.DateEditor;

public class CalendarUI extends JFrame {
    private TransactionController transactionController;
    private JSpinner dateSpinner;
    private JTextField descriptionField;
    private JTextField amountField;
    private JRadioButton incomeButton;
    private JRadioButton expenseButton;
    private JTextArea transactionDisplay;

    public CalendarUI() {

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
        UserController userController = new UserController(userName, userEmail, 1000);


        transactionController = new TransactionController();
        setTitle("Financial Budget Gamified - Transaction Logger");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        // Date Picker Setup using JSpinner
        Date today = new Date();
        SpinnerDateModel dateModel = new SpinnerDateModel(today, null, null, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        DateEditor dateEditor = new DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        
        // Create a panel for the date selection
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(new JLabel("Select Date:"), BorderLayout.NORTH);
        datePanel.add(dateSpinner, BorderLayout.CENTER);
        add(datePanel);

        descriptionField = new JTextField();
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        descPanel.add(descriptionField, BorderLayout.CENTER);
        add(descPanel);

        amountField = new JTextField();
        JPanel amountPanel = new JPanel(new BorderLayout());
        amountPanel.add(new JLabel("Amount:"), BorderLayout.NORTH);
        amountPanel.add(amountField, BorderLayout.CENTER);
        add(amountPanel);

        incomeButton = new JRadioButton("Income");
        expenseButton = new JRadioButton("Expense");
        ButtonGroup group = new ButtonGroup();
        group.add(incomeButton);
        group.add(expenseButton);
        JPanel radioPanel = new JPanel();
        radioPanel.add(incomeButton);
        radioPanel.add(expenseButton);
        add(radioPanel);

        JButton submitButton = new JButton("Log Transaction");
        add(submitButton);

        transactionDisplay = new JTextArea(10, 30);
        transactionDisplay.setEditable(false);
        add(new JScrollPane(transactionDisplay));

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logTransaction();
            }
        });

        setVisible(true);
    }

    private void logTransaction() {
        Date selectedDate = (Date) dateSpinner.getValue();
        LocalDate date = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String description = descriptionField.getText();
        double amount = Double.parseDouble(amountField.getText());
        boolean isIncome = incomeButton.isSelected();

        transactionController.addTransaction(description, amount, date, isIncome);
        updateTransactionDisplay();
    }

    private void updateTransactionDisplay() {
        transactionDisplay.setText("");
        for (Transaction t : transactionController.getTransactions()) {
            transactionDisplay.append(t.toString() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalendarUI());
    }
}
