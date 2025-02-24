package utils;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailNotifier {
    private final String senderEmail;
    private final String senderPassword;
    
    public EmailNotifier(String senderEmail, String senderPassword) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }
    
    public void sendEmail(String recipientEmail, String subject, String body) {
        // Set properties for SMTP server
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        
        // Create session with authenticator
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        
        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setText(body);
            
            // Send message
            Transport.send(message);
            System.out.println("Email sent successfully to: " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void sendBudgetExceededEmail(String userEmail, String category, double budgetLimit, double amountSpent) {
        String subject = "Budget Alert: " + category + " Budget Exceeded";
        String body = "Dear User,\n\n" +
                "Your " + category + " budget limit of $" + budgetLimit + " has been exceeded. " +
                "You have spent $" + amountSpent + " so far.\n\n" +
                "Please review your spending habits to stay within your budget.\n\n" +
                "Regards,\nFinancial Budget Gamified Team";
        
        sendEmail(userEmail, subject, body);
    }
}
