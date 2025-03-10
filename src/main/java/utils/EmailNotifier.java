package utils;

import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import model.Goal;

public class EmailNotifier {

    private static final String FROM_EMAIL = "financegame9@gmail.com"; // Do not change
    private static final String PASSWORD = "hmst zckp fwza issw"; // Do not change

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

    /**
     * Sends an email with a summary of the user's goal progress
     * 
     * @param recipientEmail The email address to send to
     * @param username The user's name
     * @param goals List of goals to include in the report
     * @return true if email was sent successfully, false otherwise
     */
    public static boolean sendGoalProgressEmail(String recipientEmail, String username, List<Goal> goals) {
        String subject = "Your Financial Goal Progress Update";
        
        // Build email content with HTML
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("<html><body style='font-family: Arial, sans-serif;'>");
        messageBuilder.append("<div style='padding: 20px; background-color: #24153A; color: white;'>");
        messageBuilder.append("<h2 style='color: #8E5AD5;'>Hello ").append(username).append("!</h2>");
        messageBuilder.append("<p>Here's an update on your financial goals:</p>");
        
        // Add a table for the goals
        messageBuilder.append("<table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>");
        messageBuilder.append("<tr style='background-color: #40247F;'>");
        messageBuilder.append("<th style='padding: 10px; text-align: left; border-bottom: 1px solid #50358F;'>Goal</th>");
        messageBuilder.append("<th style='padding: 10px; text-align: left; border-bottom: 1px solid #50358F;'>Progress</th>");
        messageBuilder.append("<th style='padding: 10px; text-align: left; border-bottom: 1px solid #50358F;'>Current/Target</th>");
        messageBuilder.append("<th style='padding: 10px; text-align: left; border-bottom: 1px solid #50358F;'>Days Left</th>");
        messageBuilder.append("<th style='padding: 10px; text-align: left; border-bottom: 1px solid #50358F;'>Status</th>");
        messageBuilder.append("</tr>");
        
        // Add each goal to the table
        for (Goal goal : goals) {
            double progress = goal.getProgressPercentage();
            String statusColor = "#27AE60"; // Green for good progress
            String statusMessage = "On Track";
            
            if (goal.isCompleted()) {
                statusColor = "#27AE60"; // Green for completed
                statusMessage = "Completed!";
            } else if (goal.isAtRisk()) {
                statusColor = "#D7263D"; // Red for at risk
                statusMessage = "At Risk";
            } else if (progress < 50 && goal.getDaysRemaining() < 15) {
                statusColor = "#EB9532"; // Orange for needs attention
                statusMessage = "Needs Attention";
            }
            
            messageBuilder.append("<tr style='background-color: #322256;'>");
            messageBuilder.append("<td style='padding: 10px; border-bottom: 1px solid #50358F;'>").append(goal.getTitle()).append("</td>");
            
            // Progress bar
            messageBuilder.append("<td style='padding: 10px; border-bottom: 1px solid #50358F;'>");
            messageBuilder.append("<div style='background-color: #50358F; width: 100%; height: 20px; border-radius: 10px;'>");
            messageBuilder.append("<div style='background-color: ").append(statusColor).append("; width: ")
                         .append(Math.min(progress, 100)).append("%; height: 20px; border-radius: 10px;'></div>");
            messageBuilder.append("</div></td>");
            
            // Current/Target amount
            messageBuilder.append("<td style='padding: 10px; border-bottom: 1px solid #50358F;'>$")
                         .append(String.format("%.2f", goal.getCurrentAmount())).append(" / $")
                         .append(String.format("%.2f", goal.getTargetAmount())).append("</td>");
            
            // Days left
            messageBuilder.append("<td style='padding: 10px; border-bottom: 1px solid #50358F;'>")
                         .append(goal.getDaysRemaining()).append(" days</td>");
            
            // Status
            messageBuilder.append("<td style='padding: 10px; border-bottom: 1px solid #50358F; color: ")
                         .append(statusColor).append(";'>").append(statusMessage).append("</td>");
            messageBuilder.append("</tr>");
        }
        
        messageBuilder.append("</table>");
        
        // Tips section
        messageBuilder.append("<div style='margin-top: 30px; padding: 15px; background-color: #40247F; border-radius: 10px;'>");
        messageBuilder.append("<h3 style='color: #8E5AD5;'>Tips to Reach Your Goals Faster:</h3>");
        messageBuilder.append("<ul>");
        messageBuilder.append("<li>Set up automatic transfers to your savings account</li>");
        messageBuilder.append("<li>Look for areas to reduce discretionary spending</li>");
        messageBuilder.append("<li>Track your expenses regularly to stay on budget</li>");
        messageBuilder.append("<li>Consider secondary income sources for goals that are at risk</li>");
        messageBuilder.append("</ul>");
        messageBuilder.append("</div>");
        
        // Footer
        messageBuilder.append("<p style='margin-top: 30px; font-size: 12px; color: #a0a0a0;'>");
        messageBuilder.append("This is an automated message from your Financial Budget Gamified App. ");
        messageBuilder.append("You can adjust your notification settings in the app.");
        messageBuilder.append("</p>");
        messageBuilder.append("</div></body></html>");
        
        String message = messageBuilder.toString();
        
        // Send the email
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                }
            });
            
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(FROM_EMAIL));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            emailMessage.setSubject(subject);
            
            // Set the email content as HTML
            emailMessage.setContent(message, "text/html; charset=utf-8");
            
            Transport.send(emailMessage);
            System.out.println("Goal progress email sent to " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send goal progress email: " + e.getMessage());
            return false;
        }
    }

    public static boolean sendNegativeBalanceEmail(String recipientEmail, String username, double balance) {
        // Ensure we only send an email if the balance is negative
        if (balance >= 0) {
            System.out.println("Balance is non-negative; no negative balance email sent.");
            return false;
        }

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
            message.setSubject("Negative Balance Alert");
            message.setText("Dear " + username + ",\n\n" +
                    "Your current balance is negative: $" + String.format("%.2f", balance) + ".\n" +
                    "Please review your expenses and take corrective action.\n\n" +
                    "Best Regards,\nFinance App");

            Transport.send(message);
            System.out.println("Negative balance email sent successfully to " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send negative balance email: " + e.getMessage());
            return false;
        }
    }


}
