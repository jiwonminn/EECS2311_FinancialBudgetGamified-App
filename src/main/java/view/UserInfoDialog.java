package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInfoDialog extends JDialog {
    private JTextField nameField;
    private JTextField emailField;
    private boolean submitted = false;

    public UserInfoDialog(JFrame parent) {
        super(parent, "Enter Your Details", true);
        setSize(300, 200);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        JButton submitButton = new JButton("Submit");
        add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(UserInfoDialog.this, "Please enter both name and email.");
                } else {
                    submitted = true;
                    setVisible(false);
                }
            }
        });

        setLocationRelativeTo(parent);
    }

    public String getUserName() {
        return nameField.getText();
    }

    public String getUserEmail() {
        return emailField.getText();
    }

    public boolean isSubmitted() {
        return submitted;
    }
}
