package view;

import controller.TransactionController;
import model.*;
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
    private JTextField amountField;
    private JRadioButton incomeButton;
    private JRadioButton expenseButton;
    private JTextArea transactionDisplay;
    private JComboBox<String> catalogBox;

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

        amountField = new JTextField();
        add(new JLabel("Amount:"));
        add(amountField);

        String[] catalogs = {"Food", "Rent", "Salary", "Shopping", "Transport", "Entertainment", "Other"};
        catalogBox = new JComboBox<>(catalogs);
        add(new JLabel("Catalog:"));
        add(catalogBox);

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
        JButton openFilterButton = new JButton("Filtering");
        add(openFilterButton);

        openFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FilterUI(transactionController);
            }
        });
        setVisible(true);
    }

    private void logTransaction() {
        Date selectedDate = (Date) datePicker.getModel().getValue();
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a date.");
            return;
        }

        LocalDate date = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String catalog = (String) catalogBox.getSelectedItem();
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
            return;
        }
        boolean isIncome = incomeButton.isSelected();

        transactionController.addTransaction(catalog, amount, date, isIncome);
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
