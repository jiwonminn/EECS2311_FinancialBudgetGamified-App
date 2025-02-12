package utils;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailNotifier {

    private static final String FROM_EMAIL = "financegame9@gmail.com"; // Replace with your email
    private static final String PASSWORD = "hmst zckp fwza issw"; // Replace with app password

    public static void sendBudgetExceededEmail(String recipientEmail, String username, double budget, double spending) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Budget Exceeded Notification");
            message.setText("Dear " + username + ",\n\n" +
                    "You have exceeded your budget!\n" +
                    "Budget Limit: $" + budget + "\n" +
                    "Actual Spending: $" + spending + "\n\n" +
                    "Please review your expenses.\n\n" +
                    "Best Regards,\nFinance App");

            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email.");
        }
    }
}
