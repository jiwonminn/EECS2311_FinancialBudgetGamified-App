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
    
    /**
     * Default constructor
     */
    public Quest() {
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
                '}';
    }
} 