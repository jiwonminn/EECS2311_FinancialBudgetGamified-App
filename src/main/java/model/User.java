package model;

public class User {
    private int id;           // Database-generated user ID
    private String username;
    private String email;
    private String password;
    private double balance;
    private int points;

    // Constructor used when creating a new user in the app (ID is not yet assigned)
    public User(String username, String email, double balance) {
        this(-1, username, email, balance, 0);
    }
    
    public User(String username, String email, String password, double balance) {
        this(-1, username, email, password, balance, 0);
    }

    // Full constructor to be used when retrieving user information from the database
    public User(int id, String username, String email, double balance, int points) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.balance = balance;
        this.points = points;
    }
    
    public User(int id, String username, String email, String password, double balance, int points) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.points = points;
    }

    public User(String email) {
        this.email = email;
    }

    // Getters
    public int getId() {
        return id;
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

    // Methods for updating state
    public void addPoints(int amount) {
        this.points += amount;
    }

    public void updateBalance(double amount) {
        this.balance += amount;
    }
}
