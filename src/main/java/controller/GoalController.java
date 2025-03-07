package controller;

import java.time.LocalDate;
import java.util.List;

import database.UserGoalDAO;
import model.UserGoal;
import utils.GoalNotifier;

/**
 * Controller class for managing financial goals
 */
public class GoalController {
    
    private int userId;
    private String username;
    private String userEmail;
    private UserGoalDAO goalDAO;
    private GoalNotifier goalNotifier;
    
    /**
     * Constructor
     * @param userId the user ID
     * @param username the username
     * @param userEmail the user's email
     */
    public GoalController(int userId, String username, String userEmail) {
        this.userId = userId;
        this.username = username;
        this.userEmail = userEmail;
        this.goalDAO = new UserGoalDAO();
        this.goalNotifier = new GoalNotifier();
    }
    
    /**
     * Creates a new saving goal
     * @param goalName the goal name
     * @param targetAmount the target amount to save
     * @param endDate the goal deadline
     * @param reminderFrequency how often to send reminders
     * @return the created goal's ID, or -1 if unsuccessful
     */
    public int createSavingGoal(String goalName, double targetAmount, LocalDate endDate, 
                               UserGoal.ReminderFrequency reminderFrequency) {
        UserGoal goal = new UserGoal(userId, UserGoal.GoalType.SAVING, goalName, targetAmount, 
                                   endDate, reminderFrequency);
        return goalDAO.addGoal(goal);
    }
    
    /**
     * Creates a new spending goal (budget)
     * @param goalName the goal name
     * @param budgetAmount the budget amount
     * @param endDate the budget deadline
     * @param reminderFrequency how often to send reminders
     * @return the created goal's ID, or -1 if unsuccessful
     */
    public int createSpendingGoal(String goalName, double budgetAmount, LocalDate endDate, 
                                 UserGoal.ReminderFrequency reminderFrequency) {
        UserGoal goal = new UserGoal(userId, UserGoal.GoalType.SPENDING, goalName, budgetAmount, 
                                   endDate, reminderFrequency);
        return goalDAO.addGoal(goal);
    }
    
    /**
     * Updates a goal's current amount
     * @param goalId the goal ID
     * @param newAmount the new current amount
     * @return true if the update was successful
     */
    public boolean updateGoalAmount(int goalId, double newAmount) {
        UserGoal goal = goalDAO.getGoalById(goalId);
        if (goal == null) {
            return false;
        }
        
        // Update the current amount
        goal.setCurrentAmount(newAmount);
        
        // Check if the goal is now completed
        boolean wasCompleted = goal.isCompleted();
        goal.updateCompletionStatus();
        boolean isNowCompleted = goal.isCompleted();
        
        // Save the updated goal
        boolean updated = goalDAO.updateGoal(goal);
        
        // If the goal was just completed, send a notification
        if (!wasCompleted && isNowCompleted) {
            goalNotifier.sendGoalAchievedNotification(userEmail, username, goal);
        }
        
        return updated;
    }
    
    /**
     * Gets all goals for the current user
     * @return a list of all user goals
     */
    public List<UserGoal> getAllGoals() {
        return goalDAO.getGoalsByUserId(userId);
    }
    
    /**
     * Gets all incomplete goals for the current user
     * @return a list of incomplete goals
     */
    public List<UserGoal> getIncompleteGoals() {
        return goalDAO.getIncompleteGoalsByUserId(userId);
    }
    
    /**
     * Deletes a goal
     * @param goalId the ID of the goal to delete
     * @return true if the deletion was successful
     */
    public boolean deleteGoal(int goalId) {
        return goalDAO.deleteGoal(goalId);
    }
    
    /**
     * Processes reminders for all incomplete goals
     * Sends reminder emails for goals that need them
     */
    public void processReminders() {
        List<UserGoal> incompleteGoals = getIncompleteGoals();
        
        for (UserGoal goal : incompleteGoals) {
            if (goal.shouldSendReminder()) {
                if (goal.getGoalType() == UserGoal.GoalType.SAVING) {
                    goalNotifier.sendSavingGoalReminder(userEmail, username, goal);
                } else {
                    goalNotifier.sendSpendingGoalReminder(userEmail, username, goal);
                }
            }
        }
    }
} 