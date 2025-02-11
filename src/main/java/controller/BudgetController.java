package main.java.controller;

import main.java.model.Budget;
import utils.EmailNotifier;

public class BudgetController {
    private Budget budget;

    public BudgetController(double monthlyLimit, double weeklyLimit) {
        this.budget = new Budget(monthlyLimit, weeklyLimit);
    }

    public boolean isOverWeeklyBudget(double expense) {
        return expense > budget.getWeeklyLimit();
    }

    public boolean isOverMonthlyBudget(double expense) {
        return expense > budget.getMonthlyLimit();
    }

    public void checkBudgetAndNotify(String recipientEmail, String username, double expense) {
        if (isOverWeeklyBudget(expense)) {
            EmailNotifier.sendBudgetExceededEmail(recipientEmail, username, budget.getWeeklyLimit(), expense);
        }
        if (isOverMonthlyBudget(expense)) {
            EmailNotifier.sendBudgetExceededEmail(recipientEmail, username, budget.getMonthlyLimit(), expense);
        }
    }

    public void updateBudget(double monthly, double weekly) {
        budget.setMonthlyLimit(monthly);
        budget.setWeeklyLimit(weekly);
    }
}
