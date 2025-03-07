package model;

import java.util.Date;

/**
 * Represents a financial goal for a user.
 */
public class Goal {
    private int id;
    private int userId;
    private String title;
    private String description;
    private double targetAmount;
    private double currentAmount;
    private Date startDate;
    private Date targetDate;
    private String category;
    private boolean completed;

    /**
     * Constructor for creating a new goal
     */
    public Goal(int userId, String title, String description, double targetAmount, 
                Date startDate, Date targetDate, String category) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.targetAmount = targetAmount;
        this.currentAmount = 0.0;
        this.startDate = startDate;
        this.targetDate = targetDate;
        this.category = category;
        this.completed = false;
    }

    /**
     * Constructor for loading a goal from database
     */
    public Goal(int id, int userId, String title, String description, double targetAmount, 
                double currentAmount, Date startDate, Date targetDate, String category, 
                boolean completed) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.startDate = startDate;
        this.targetDate = targetDate;
        this.category = category;
        this.completed = completed;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
        // Auto-update completion status
        this.completed = currentAmount >= targetAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Calculate the progress percentage toward the goal
     * @return Progress as a percentage (0-100)
     */
    public double getProgressPercentage() {
        if (targetAmount <= 0) {
            return 0;
        }
        double progress = (currentAmount / targetAmount) * 100;
        return Math.min(progress, 100.0); // Cap at 100%
    }

    /**
     * Calculate days remaining until target date
     * @return Number of days remaining, or 0 if target date has passed
     */
    public long getDaysRemaining() {
        if (targetDate == null) {
            return 0;
        }
        
        long currentTime = new Date().getTime();
        long targetTime = targetDate.getTime();
        long diffTime = targetTime - currentTime;
        
        if (diffTime <= 0) {
            return 0;
        }
        
        return diffTime / (24 * 60 * 60 * 1000); // Convert milliseconds to days
    }

    /**
     * Determine if a goal is at risk (less than 30% progress with less than 30% time remaining)
     * @return true if the goal is at risk
     */
    public boolean isAtRisk() {
        if (completed) {
            return false;
        }
        
        // Calculate total duration in days
        long totalDuration = (targetDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
        if (totalDuration <= 0) {
            return false;
        }
        
        // Calculate percentage of time elapsed
        long daysRemaining = getDaysRemaining();
        long daysElapsed = totalDuration - daysRemaining;
        double timePercentageElapsed = (double) daysElapsed / totalDuration * 100;
        
        // If more than 70% of time has elapsed but less than 70% progress, it's at risk
        return timePercentageElapsed > 70 && getProgressPercentage() < 70;
    }
} 