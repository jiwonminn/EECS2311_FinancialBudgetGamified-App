package view;

import controller.TransactionController;
import model.Transaction;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

public class FilterUI extends JFrame {
    private TransactionController transactionController;
    private JDatePickerImpl startDatePicker, endDatePicker;
    private JComboBox<String> catalogFilterBox, sortOptionBox;
    private JTextArea transactionDisplay;

    public FilterUI(TransactionController controller) {
        this.transactionController = controller;

        setTitle("Filter Transactions");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Date Pickers
        startDatePicker = createDatePicker();
        endDatePicker = createDatePicker();

        JPanel datePanel = new JPanel();
        datePanel.add(new JLabel("From:"));
        datePanel.add(startDatePicker);
        datePanel.add(new JLabel("To:"));
        datePanel.add(endDatePicker);
        add(datePanel);

        // Category Filter
        String[] catalogs = {"All", "Food", "Rent", "Salary", "Shopping", "Transport", "Entertainment", "Other"};
        catalogFilterBox = new JComboBox<>(catalogs);
        add(new JLabel("Select Category:"));
        add(catalogFilterBox);

        // Sort Options
        String[] sortOptions = {"Date (Newest First)", "Amount (Highest First)"};
        sortOptionBox = new JComboBox<>(sortOptions);
        add(new JLabel("Sort By:"));
        add(sortOptionBox);

        // Filter Button
        JButton filterButton = new JButton("Filter Transactions");
        add(filterButton);

        // Transaction Display
        transactionDisplay = new JTextArea(10, 30);
        transactionDisplay.setEditable(false);
        add(new JScrollPane(transactionDisplay));

        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterTransactions();
            }
        });

        setVisible(true);
    }

    private JDatePickerImpl createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    private void filterTransactions() {
        // Get selected dates
        Date start = (Date) startDatePicker.getModel().getValue();
        Date end = (Date) endDatePicker.getModel().getValue();
        LocalDate startDate = (start != null) ? start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate endDate = (end != null) ? end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        // Get selected category
        String selectedCatalog = (String) catalogFilterBox.getSelectedItem();

        // Get sorting option
        String selectedSort = (String) sortOptionBox.getSelectedItem();
        boolean sortByAmount = selectedSort.equals("Amount (Highest First)");

        // Fetch transactions
        List<Transaction> transactions = transactionController.getTransactionsByFilters(startDate, endDate, selectedCatalog, sortByAmount);

        // Update display
        updateTransactionDisplay(transactions);
    }

    private void updateTransactionDisplay(List<Transaction> transactions) {
        transactionDisplay.setText("");
        for (Transaction t : transactions) {
            transactionDisplay.append(t.toString() + "\n");
        }
    }
}
