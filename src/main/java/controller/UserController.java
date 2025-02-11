package main.java.controller;

import main.java.model.User;

public class UserController {
    private User user;

    public UserController(String username, String email, double balance) {
        this.user = new User(username, email, balance);
    }

    public void addPoints(int points) {
        user.addPoints(points);
    }

    public void updateBalance(double amount) {
        user.updateBalance(amount);
    }

    public String getUserInfo() {
        return "User: " + user.getUsername() + ", Balance: " + user.getBalance() + ", Points: " + user.getPoints();
    }
}
