package model;

public class User {
    private String username;
    private double balance;
    private int points;

    public User(String username, double balance) {
        this.username = username;
        this.balance = balance;
        this.points = 0;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int amount) {
        this.points += amount;
    }

    public void updateBalance(double amount) {
        this.balance += amount;
    }
}
