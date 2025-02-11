package main.java.model;

public class User {
    private String username;
    private String email;
    private double balance;
    private int points;

    public User(String username, String email, double balance) {
        this.username = username;
        this.email = email;
        this.balance = balance;
        this.points = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
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
