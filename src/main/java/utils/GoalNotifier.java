package utils;

import java.time.LocalDate;

import database.UserGoalDAO;
import model.UserGoal;

/**
 * Utility for sending goal-related email notifications
 */
public class GoalNotifier {
    
    private UserGoalDAO goalDAO;
    
    public GoalNotifier() {
        this.goalDAO = new UserGoalDAO();
    }
    
    /**
     * Sends a reminder for a saving goal
     * @param userEmail the user's email
     * @param username the username
     * @param goal the saving goal
     * @return true if the email was sent successfully
     */
    public boolean sendSavingGoalReminder(String userEmail, String username, UserGoal goal) {
        if (goal == null || !goal.shouldSendReminder()) {
            return false;
        }
        
        try {
            String subject = "Saving Goal Reminder: " + goal.getGoalName();
            
            long daysLeft = goal.getDaysLeft();
            double progressPercentage = goal.getProgressPercentage();
            double remaining = goal.getTargetAmount() - goal.getCurrentAmount();
            
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Dear ").append(username).append(",\n\n");
            messageBuilder.append("This is a reminder about your saving goal: ").append(goal.getGoalName()).append("\n\n");
            messageBuilder.append("Goal Details:\n");
            messageBuilder.append("Target Amount: $").append(String.format("%.2f", goal.getTargetAmount())).append("\n");
            messageBuilder.append("Current Amount: $").append(String.format("%.2f", goal.getCurrentAmount())).append("\n");
            messageBuilder.append("Amount Remaining: $").append(String.format("%.2f", remaining)).append("\n");
            messageBuilder.append("Progress: ").append(String.format("%.1f", progressPercentage)).append("%\n");
            messageBuilder.append("Days Left: ").append(daysLeft).append("\n\n");
            
            if (daysLeft <= 7) {
                messageBuilder.append("⚠️ Your goal deadline is approaching! Only ").append(daysLeft)
                             .append(" days left to reach your target.\n\n");
            }
            
            messageBuilder.append("Keep up the good work and stay on track to meet your financial goals!\n\n");
            messageBuilder.append("Best Regards,\nFinance App");
            
            EmailNotifier.sendCustomEmail(userEmail, username, subject, messageBuilder.toString());
            
            // Update the last reminder date
            goalDAO.updateLastReminderDate(goal.getId(), LocalDate.now());
            
            return true;
        } catch (Exception e) {
            System.err.println("Error sending saving goal reminder: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sends a reminder for a spending goal
     * @param userEmail the user's email
     * @param username the username
     * @param goal the spending goal
     * @return true if the email was sent successfully
     */
    public boolean sendSpendingGoalReminder(String userEmail, String username, UserGoal goal) {
        if (goal == null || !goal.shouldSendReminder()) {
            return false;
        }
        
        try {
            String subject = "Spending Goal Reminder: " + goal.getGoalName();
            
            long daysLeft = goal.getDaysLeft();
            double progressPercentage = goal.getProgressPercentage();
            double remaining = goal.getTargetAmount() - goal.getCurrentAmount();
            
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Dear ").append(username).append(",\n\n");
            messageBuilder.append("This is a reminder about your spending goal: ").append(goal.getGoalName()).append("\n\n");
            messageBuilder.append("Goal Details:\n");
            messageBuilder.append("Budget Amount: $").append(String.format("%.2f", goal.getTargetAmount())).append("\n");
            messageBuilder.append("Current Spending: $").append(String.format("%.2f", goal.getCurrentAmount())).append("\n");
            
            if (remaining >= 0) {
                messageBuilder.append("Budget Remaining: $").append(String.format("%.2f", remaining)).append("\n");
                messageBuilder.append("Budget Status: ").append(String.format("%.1f", progressPercentage)).append("% remaining\n");
            } else {
                messageBuilder.append("Budget Exceeded by: $").append(String.format("%.2f", -remaining)).append("\n");
                messageBuilder.append("Budget Status: Exceeded by ").append(String.format("%.1f", (-remaining/goal.getTargetAmount())*100)).append("%\n");
            }
            
            messageBuilder.append("Days Left: ").append(daysLeft).append("\n\n");
            
            if (remaining < 0) {
                messageBuilder.append("⚠️ You have exceeded your spending budget! Consider adjusting your spending habits.\n\n");
            } else if (daysLeft > 7 && remaining < (goal.getTargetAmount() * 0.25)) {
                messageBuilder.append("⚠️ You have less than 25% of your budget remaining with ").append(daysLeft)
                             .append(" days left in your budget period. Consider slowing down your spending.\n\n");
            } else if (daysLeft <= 7) {
                messageBuilder.append("⚠️ Your budget period is ending soon! Only ").append(daysLeft)
                             .append(" days left with $").append(String.format("%.2f", remaining)).append(" remaining.\n\n");
            }
            
            messageBuilder.append("Keep a close eye on your spending to stay within your budget!\n\n");
            messageBuilder.append("Best Regards,\nFinance App");
            
            EmailNotifier.sendCustomEmail(userEmail, username, subject, messageBuilder.toString());
            
            // Update the last reminder date
            goalDAO.updateLastReminderDate(goal.getId(), LocalDate.now());
            
            return true;
        } catch (Exception e) {
            System.err.println("Error sending spending goal reminder: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sends a goal achieved notification
     * @param userEmail the user's email
     * @param username the username
     * @param goal the completed goal
     * @return true if the email was sent successfully
     */
    public boolean sendGoalAchievedNotification(String userEmail, String username, UserGoal goal) {
        if (goal == null || !goal.isCompleted()) {
            return false;
        }
        
        try {
            String subject = "Congratulations! Goal Achieved: " + goal.getGoalName();
            
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Dear ").append(username).append(",\n\n");
            messageBuilder.append("🎉 CONGRATULATIONS! You have achieved your ");
            
            if (goal.getGoalType() == UserGoal.GoalType.SAVING) {
                messageBuilder.append("saving goal: ");
            } else {
                messageBuilder.append("spending goal: ");
            }
            
            messageBuilder.append(goal.getGoalName()).append("\n\n");
            messageBuilder.append("Goal Details:\n");
            
            if (goal.getGoalType() == UserGoal.GoalType.SAVING) {
                messageBuilder.append("Target Amount: $").append(String.format("%.2f", goal.getTargetAmount())).append("\n");
                messageBuilder.append("Amount Saved: $").append(String.format("%.2f", goal.getCurrentAmount())).append("\n");
                messageBuilder.append("You successfully saved the target amount! Great job on your financial discipline.\n\n");
            } else {
                messageBuilder.append("Budget Amount: $").append(String.format("%.2f", goal.getTargetAmount())).append("\n");
                messageBuilder.append("Actual Spending: $").append(String.format("%.2f", goal.getCurrentAmount())).append("\n");
                messageBuilder.append("You successfully stayed within your budget! Great job on your financial discipline.\n\n");
            }
            
            messageBuilder.append("This is a significant milestone in your financial journey. Keep up the excellent work!\n\n");
            messageBuilder.append("Best Regards,\nFinance App");
            
            EmailNotifier.sendCustomEmail(userEmail, username, subject, messageBuilder.toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error sending goal achieved notification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 