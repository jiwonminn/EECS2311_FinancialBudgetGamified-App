package main.java.model;

public class Budget {
    private double monthlyLimit;
    private double weeklyLimit;

    public Budget(double monthlyLimit, double weeklyLimit) {
        this.monthlyLimit = monthlyLimit;
        this.weeklyLimit = weeklyLimit;
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    public double getWeeklyLimit() {
        return weeklyLimit;
    }

    public void setMonthlyLimit(double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public void setWeeklyLimit(double weeklyLimit) {
        this.weeklyLimit = weeklyLimit;
    }
}
