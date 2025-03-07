package model;

import java.time.LocalDate;

/**
 * Represents a user's financial goal (saving or spending).
 */
public class UserGoal {
    
    public enum GoalType {
        SAVING, SPENDING
    }
    
    public enum ReminderFrequency {
        DAILY, WEEKLY, MONTHLY
    }
    
    private int id;
    private int userId;
    private GoalType goalType;
    private String goalName;
    private double targetAmount;
    private double currentAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCompleted;
    private ReminderFrequency reminderFrequency;
    private LocalDate lastReminderDate;
    private LocalDate createdAt;
    
    // Constructor
    public UserGoal(int userId, GoalType goalType, String goalName, double targetAmount, 
                   LocalDate endDate, ReminderFrequency reminderFrequency) {
        this.userId = userId;
        this.goalType = goalType;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = 0.0;
        this.startDate = LocalDate.now();
        this.endDate = endDate;
        this.isCompleted = false;
        this.reminderFrequency = reminderFrequency;
        this.createdAt = LocalDate.now();
    }
    
    // Full constructor
    public UserGoal(int id, int userId, GoalType goalType, String goalName, 
                   double targetAmount, double currentAmount, LocalDate startDate, 
                   LocalDate endDate, boolean isCompleted, ReminderFrequency reminderFrequency,
                   LocalDate lastReminderDate, LocalDate createdAt) {
        this.id = id;
        this.userId = userId;
        this.goalType = goalType;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCompleted = isCompleted;
        this.reminderFrequency = reminderFrequency;
        this.lastReminderDate = lastReminderDate;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
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
    
    public GoalType getGoalType() {
        return goalType;
    }
    
    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }
    
    public String getGoalName() {
        return goalName;
    }
    
    public void setGoalName(String goalName) {
        this.goalName = goalName;
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
        // Check if goal is completed
        updateCompletionStatus();
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    
    public ReminderFrequency getReminderFrequency() {
        return reminderFrequency;
    }
    
    public void setReminderFrequency(ReminderFrequency reminderFrequency) {
        this.reminderFrequency = reminderFrequency;
    }
    
    public LocalDate getLastReminderDate() {
        return lastReminderDate;
    }
    
    public void setLastReminderDate(LocalDate lastReminderDate) {
        this.lastReminderDate = lastReminderDate;
    }
    
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    
    /**
     * Updates the completion status based on current amount and goal type
     */
    public void updateCompletionStatus() {
        if (goalType == GoalType.SAVING) {
            // For saving goals, completed when current >= target
            isCompleted = currentAmount >= targetAmount;
        } else {
            // For spending goals, completed when current <= target (staying under budget)
            isCompleted = currentAmount <= targetAmount;
        }
    }
    
    /**
     * Calculates progress as a percentage
     * @return progress percentage (0-100)
     */
    public double getProgressPercentage() {
        if (targetAmount == 0) return 0;
        
        double progress;
        if (goalType == GoalType.SAVING) {
            // For saving goals, progress is currentAmount/targetAmount
            progress = Math.min(100, (currentAmount / targetAmount) * 100);
        } else {
            // For spending goals, progress is reversed (lower is better)
            if (currentAmount <= targetAmount) {
                // Under budget
                progress = Math.min(100, ((targetAmount - currentAmount) / targetAmount) * 100);
            } else {
                // Over budget
                progress = 0;
            }
        }
        return Math.round(progress * 100) / 100.0; // Round to 2 decimal places
    }
    
    /**
     * Checks if it's time to send a reminder based on frequency
     * @return true if reminder should be sent
     */
    public boolean shouldSendReminder() {
        if (isCompleted) return false;
        
        LocalDate today = LocalDate.now();
        
        // If no previous reminder has been sent
        if (lastReminderDate == null) {
            return true;
        }
        
        switch (reminderFrequency) {
            case DAILY:
                return !lastReminderDate.equals(today);
            case WEEKLY:
                return lastReminderDate.plusDays(7).isBefore(today) || lastReminderDate.plusDays(7).equals(today);
            case MONTHLY:
                return lastReminderDate.plusMonths(1).isBefore(today) || lastReminderDate.plusMonths(1).equals(today);
            default:
                return false;
        }
    }
    
    /**
     * Calculate days left until goal deadline
     * @return number of days left
     */
    public long getDaysLeft() {
        return LocalDate.now().until(endDate).getDays();
    }
} 