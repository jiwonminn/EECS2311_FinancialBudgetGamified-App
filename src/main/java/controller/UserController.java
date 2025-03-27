package controller;

import model.User;
import utils.SessionManager;

public class UserController {
    private User user;
    private int userId;

    public UserController(String username, String email, double balance) {
        this.user = new User(username, email, balance);
        // Default user ID, should be updated from database when available
        this.userId = 1;
    }
    
    /**
     * Constructor with user ID
     */
    public UserController(int userId, String username, String email, double balance) {
        this.user = new User(username, email, balance);
        this.userId = userId;
    }

    public void addPoints(int points) {
        user.addPoints(points);
    }

    public void updateBalance(double amount) {
        user.updateBalance(amount);
    }
    
    public int getPoints() {
        return user.getPoints();
    }
    
    /**
     * Get the user ID
     * @return the user ID
     */
    public int getUserId() {
        // Try to get from SessionManager first if ID is default
        if (userId == 1) {
            try {
                int sessionUserId = SessionManager.getInstance().getUserId();
                if (sessionUserId > 0) {
                    userId = sessionUserId;
                }
            } catch (Exception e) {
                // Silently fail, use default ID
            }
        }
        return userId;
    }
    
    /**
     * Set the user ID
     * @param userId the user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserInfo() {
        return "User: " + user.getUsername() + ", Email: "+ user.getEmail()+ ", Balance: " + user.getBalance() + ", Points: " + user.getPoints();
    }
}
