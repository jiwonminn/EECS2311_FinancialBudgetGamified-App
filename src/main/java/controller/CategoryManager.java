package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages categories across the application.
 * This class follows the Singleton pattern.
 */
public class CategoryManager {
    private static CategoryManager instance;
    private final List<String> defaultCategories = Arrays.asList(
            "Housing", "Food", "Transportation", "Entertainment",
            "Healthcare", "Education", "Savings", "Income", "Other"
    );
    private final Set<String> allCategories = new HashSet<>();
    private final List<CategoryChangeListener> listeners = new ArrayList<>();

    public interface CategoryChangeListener {
        void onCategoriesChanged(List<String> categories);
    }

    private CategoryManager() {
        allCategories.addAll(defaultCategories);
    }

    public static synchronized CategoryManager getInstance() {
        if (instance == null) {
            instance = new CategoryManager();
        }
        return instance;
    }

    public void addListener(CategoryChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(CategoryChangeListener listener) {
        listeners.remove(listener);
    }

    public List<String> getAllCategories() {
        List<String> sortedCategories = new ArrayList<>(allCategories);
        Collections.sort(sortedCategories);
        return sortedCategories;
    }

    public void addCategory(String category) {
        if (category != null && !category.trim().isEmpty() && !allCategories.contains(category)) {
            allCategories.add(category);
            notifyListeners();
        }
    }

    private void notifyListeners() {
        List<String> sortedCategories = getAllCategories();
        for (CategoryChangeListener listener : listeners) {
            listener.onCategoriesChanged(sortedCategories);
        }
    }
}
