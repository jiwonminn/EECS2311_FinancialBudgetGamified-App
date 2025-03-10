package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages categories across the application and provides category synchronization.
 * This class follows the Singleton pattern to ensure all components use the same categories.
 */
public class CategoryManager {
    private static CategoryManager instance;
    
    // Default categories available in the application
    private final List<String> defaultCategories = Arrays.asList(
        "Housing", "Food", "Transportation", "Entertainment", 
        "Healthcare", "Education", "Savings", "Income", "Other"
    );
    
    // All categories including custom ones from goals
    private final Set<String> allCategories = new HashSet<>();
    
    // Listeners to notify when categories change
    private final List<CategoryChangeListener> listeners = new ArrayList<>();
    
    /**
     * Interface for components that need to know when categories change
     */
    public interface CategoryChangeListener {
        void onCategoriesChanged(List<String> categories);
    }
    
    /**
     * Private constructor to enforce singleton pattern
     */
    private CategoryManager() {
        // Initialize with default categories
        allCategories.addAll(defaultCategories);
    }
    
    /**
     * Get the singleton instance
     */
    public static synchronized CategoryManager getInstance() {
        if (instance == null) {
            instance = new CategoryManager();
        }
        return instance;
    }
    
    /**
     * Register a listener to be notified when categories change
     */
    public void addListener(CategoryChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove a listener
     */
    public void removeListener(CategoryChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Get all categories as a sorted list
     */
    public List<String> getAllCategories() {
        List<String> sortedCategories = new ArrayList<>(allCategories);
        Collections.sort(sortedCategories);
        return sortedCategories;
    }
    
    /**
     * Add a new category and notify listeners
     */
    public void addCategory(String category) {
        if (category != null && !category.trim().isEmpty() && !allCategories.contains(category)) {
            allCategories.add(category);
            notifyListeners();
        }
    }
    
    /**
     * Notify all registered listeners about category changes
     */
    private void notifyListeners() {
        List<String> sortedCategories = getAllCategories();
        for (CategoryChangeListener listener : listeners) {
            listener.onCategoriesChanged(sortedCategories);
        }
    }
} 