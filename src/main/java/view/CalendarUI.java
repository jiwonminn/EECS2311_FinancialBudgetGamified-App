package view;

import com.toedter.calendar.JDateChooser;
import controller.TransactionController;
import controller.UserController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CalendarUI extends JFrame {
    private TransactionController transactionController;
    private JDateChooser dateChooser;
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        // Date Chooser Setup
        dateChooser = new JDateChooser();
        dateChooser.setDate(new Date());
        
        add(new JLabel("Select Date:"));
        add(dateChooser);

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
        Date selectedDate = dateChooser.getDate();
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
