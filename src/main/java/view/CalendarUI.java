package main.java.view;

import main.java.controller.TransactionController;
import main.java.model.Transaction;
import org.jdatepicker.JDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class CalendarUI extends JFrame {
    private TransactionController transactionController;
    private JDatePicker datePicker;
    private JTextField descriptionField;
    private JTextField amountField;
    private JRadioButton incomeButton;
    private JRadioButton expenseButton;
    private JTextArea transactionDisplay;

    public CalendarUI() {
        transactionController = new TransactionController();
        setTitle("Financial Budget Gamified - Transaction Logger");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        // Date Picker
        datePicker = new JDatePicker();
        add(new JLabel("Select Date:"));
        add((Component) datePicker);

        // Transaction Description
        descriptionField = new JTextField();
        add(new JLabel("Description:"));
        add(descriptionField);

        // Amount Input
        amountField = new JTextField();
        add(new JLabel("Amount:"));
        add(amountField);

        // Income/Expense Selection
        incomeButton = new JRadioButton("Income");
        expenseButton = new JRadioButton("Expense");
        ButtonGroup group = new ButtonGroup();
        group.add(incomeButton);
        group.add(expenseButton);
        JPanel radioPanel = new JPanel();
        radioPanel.add(incomeButton);
        radioPanel.add(expenseButton);
        add(radioPanel);

        // Submit Button
        JButton submitButton = new JButton("Log Transaction");
        add(submitButton);

        // Transaction Display
        transactionDisplay = new JTextArea(10, 30);
        transactionDisplay.setEditable(false);
        add(new JScrollPane(transactionDisplay));

        // Button Click Listener
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logTransaction();
            }
        });

        setVisible(true);
    }

    private void logTransaction() {
        try {
            LocalDate date = LocalDate.parse(datePicker.getFormattedTextField().getText());
            String description = descriptionField.getText();
            double amount = Double.parseDouble(amountField.getText());
            boolean isIncome = incomeButton.isSelected();

            transactionController.addTransaction(description, amount, date, isIncome);
            updateTransactionDisplay();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check your entries.");
        }
    }

    private void updateTransactionDisplay() {
        List<Transaction> transactions = transactionController.getTransactions();
        transactionDisplay.setText("");
        for (Transaction t : transactions) {
            transactionDisplay.append(t.toString() + "\n");
        }
    }

    public static void main(String[] args) {
        new CalendarUI();
    }
}
