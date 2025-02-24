package controller;

import model.Budget;
import utils.EmailNotifier;
import jakarta.mail.MessagingException;

public class BudgetController {
    private double monthlyLimit;
    private double weeklyLimit;
    private EmailNotifier emailNotifier;

    public BudgetController(double monthlyLimit, double weeklyLimit) {
        this.monthlyLimit = monthlyLimit;
        this.weeklyLimit = weeklyLimit;
        // Initialize with your email credentials
        this.emailNotifier = new EmailNotifier("your-email@gmail.com", "your-app-password");
    }

    public boolean isOverWeeklyBudget(double expense) {
        return expense > weeklyLimit;
    }

    public boolean isOverMonthlyBudget(double expense) {
        return expense > monthlyLimit;
    }

    public void checkBudget(String userEmail, double currentSpending) {
        try {
            if (currentSpending > monthlyLimit) {
                emailNotifier.sendBudgetExceededEmail(userEmail, "Monthly", monthlyLimit, currentSpending);
            }
            if (currentSpending > weeklyLimit) {
                emailNotifier.sendBudgetExceededEmail(userEmail, "Weekly", weeklyLimit, currentSpending);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void updateBudget(double monthly, double weekly) {
        monthlyLimit = monthly;
        weeklyLimit = weekly;
    }

    public void checkBudgetAndNotify(String userEmail, String category, double amount) {
        try {
            if (category.equalsIgnoreCase("monthly") && amount > monthlyLimit) {
                emailNotifier.sendBudgetExceededEmail(userEmail, "Monthly", monthlyLimit, amount);
            } else if (category.equalsIgnoreCase("weekly") && amount > weeklyLimit) {
                emailNotifier.sendBudgetExceededEmail(userEmail, "Weekly", weeklyLimit, amount);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
