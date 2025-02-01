package controller;

import model.Budget;

public class BudgetController {
    private Budget budget;

    public BudgetController(double monthlyLimit, double weeklyLimit) {
        this.budget = new Budget(monthlyLimit, weeklyLimit);
    }

    public boolean isOverBudget(double expense) {
        return expense > budget.getWeeklyLimit();
    }

    public void updateBudget(double monthly, double weekly) {
        budget.setMonthlyLimit(monthly);
        budget.setWeeklyLimit(weekly);
    }
}
