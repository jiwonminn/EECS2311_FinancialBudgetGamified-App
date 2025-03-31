package model;

import java.time.LocalDate;

/**
 * Represents a quest in the financial budgeting app
 */
public class Quest {
    private int id;
    private String title;
    private String description;
    private String questType; // DAILY, WEEKLY, MONTHLY
    private int xpReward;
    private double requiredAmount;
    private boolean completed;
    private LocalDate deadline;
    private int userId;
    private int progress; // Progress value from 0-100
    
    /**
     * Default constructor
     */
    public Quest() {
        this.progress = 0; // Default progress to 0
    }
    
    /**
     * Constructor with all fields
     */
    public Quest(int id, String title, String description, String questType, int xpReward, 
                double requiredAmount, boolean completed, LocalDate deadline, int userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.questType = questType;
        this.xpReward = xpReward;
        this.requiredAmount = requiredAmount;
        this.completed = completed;
        this.deadline = deadline;
        this.userId = userId;
        this.progress = completed ? 100 : 0; // Set progress to 100 if completed, 0 otherwise
    }
    
    // Getters and setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    public String getQuestType() {
        return questType;
    }
    
    public void setQuestType(String questType) {
        this.questType = questType;
    }
    
    public int getXpReward() {
        return xpReward;
    }
    
    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }
    
    public double getRequiredAmount() {
        return requiredAmount;
    }
    
    public void setRequiredAmount(double requiredAmount) {
        this.requiredAmount = requiredAmount;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed) {
            this.progress = 100; // Set progress to 100% when completed
        }
    }
    
    public LocalDate getDeadline() {
        return deadline;
    }
    
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    /**
     * Get the current progress of this quest (0-100)
     * @return Progress percentage from 0 to 100
     */
    public int getProgress() {
        return progress;
    }
    
    /**
     * Set the current progress of this quest
     * @param progress Progress percentage from 0 to 100
     */
    public void setProgress(int progress) {
        // Ensure progress is between 0 and 100
        if (progress < 0) {
            this.progress = 0;
        } else if (progress > 100) {
            this.progress = 100;
        } else {
            this.progress = progress;
        }
        
        // Update completion status if progress is 100%
        if (this.progress == 100) {
            this.completed = true;
        }
    }
    
    @Override
    public String toString() {
        return "Quest{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", questType='" + questType + '\'' +
                ", xpReward=" + xpReward +
                ", requiredAmount=" + requiredAmount +
                ", completed=" + completed +
                ", deadline=" + deadline +
                ", userId=" + userId +
                ", progress=" + progress +
                '}';
    }
} 