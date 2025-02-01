package controller;

import model.User;

public class UserController {
    private User user;

    public UserController(String username, double balance) {
        this.user = new User(username, balance);
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
