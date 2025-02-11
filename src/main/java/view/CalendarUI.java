package main.java.view;

import main.java.controller.TransactionController;
import main.java.model.*;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;

public class CalendarUI extends JFrame {
    private TransactionController transactionController;
    private JDatePickerImpl datePicker;
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

        // Date Picker Setup
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        add(new JLabel("Select Date:"));
        add(datePicker);

        descriptionField = new JTextField();
        add(new JLabel("Description:"));
        add(descriptionField);

        amountField = new JTextField();
        add(new JLabel("Amount:"));
        add(amountField);

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
        Date selectedDate = (Date) datePicker.getModel().getValue();
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
        new CalendarUI();
    }
}
