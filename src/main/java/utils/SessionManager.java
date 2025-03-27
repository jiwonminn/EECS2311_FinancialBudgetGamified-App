package utils;

/**
 * SessionManager class to keep track of currently logged in user
 * Uses singleton pattern for global access
 */
public class SessionManager {
    private static SessionManager instance;
    private int userId = -1;
    private String username = "";
    private String email = "";
    
    // Private constructor to prevent direct instantiation
    private SessionManager() {
        // Initialize with default values
    }
    
    /**
     * Get the singleton instance of SessionManager
     * @return the SessionManager instance
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Set the current user's session details
     * @param userId the user ID
     * @param username the username
     * @param email the user's email
     */
    public void setCurrentUser(int userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
    
    /**
     * Clear the current user session
     */
    public void clearSession() {
        this.userId = -1;
        this.username = "";
        this.email = "";
    }
    
    /**
     * Get the current user ID
     * @return the user ID or -1 if not logged in
     */
    public int getUserId() {
        return this.userId;
    }
    
    /**
     * Get the current username
     * @return the username or empty string if not logged in
     */
    public String getUsername() {
        return this.username;
    }
    
    /**
     * Get the current user's email
     * @return the email or empty string if not logged in
     */
    public String getEmail() {
        return this.email;
    }
    
    /**
     * Check if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return this.userId > 0;
    }
} 