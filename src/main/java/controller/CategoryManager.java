package controller;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private final Map<String, CategoryIcon> categoryIcons = new HashMap<>();

    public interface CategoryChangeListener {
        void onCategoriesChanged(List<String> categories);
    }
    
    public static class CategoryIcon {
        private final IconType type;
        private final Color color;
        
        public CategoryIcon(IconType type, Color color) {
            this.type = type;
            this.color = color;
        }
        
        public IconType getType() {
            return type;
        }
        
        public Color getColor() {
            return color;
        }
    }
    
    public enum IconType {
        HOME, FOOD, CAR, ENTERTAINMENT, HEALTH, EDUCATION, 
        SAVINGS, INCOME, SHOPPING, BILL, GIFT, TRAVEL, 
        FITNESS, TECHNOLOGY, CUSTOM, OTHER
    }

    private CategoryManager() {
        allCategories.addAll(defaultCategories);
        
        // Initialize default category icons
        initializeDefaultIcons();
    }
    
    private void initializeDefaultIcons() {
        categoryIcons.put("Housing", new CategoryIcon(IconType.HOME, new Color(68, 189, 190)));
        categoryIcons.put("Food", new CategoryIcon(IconType.FOOD, new Color(231, 76, 60)));
        categoryIcons.put("Transportation", new CategoryIcon(IconType.CAR, new Color(52, 152, 219)));
        categoryIcons.put("Entertainment", new CategoryIcon(IconType.ENTERTAINMENT, new Color(155, 89, 182)));
        categoryIcons.put("Healthcare", new CategoryIcon(IconType.HEALTH, new Color(46, 204, 113)));
        categoryIcons.put("Education", new CategoryIcon(IconType.EDUCATION, new Color(241, 196, 15)));
        categoryIcons.put("Savings", new CategoryIcon(IconType.SAVINGS, new Color(230, 126, 34)));
        categoryIcons.put("Income", new CategoryIcon(IconType.INCOME, new Color(39, 174, 96)));
        categoryIcons.put("Other", new CategoryIcon(IconType.OTHER, new Color(149, 165, 166)));
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
        addCategory(category, new CategoryIcon(IconType.CUSTOM, new Color(149, 165, 166)));
    }
    
    public void addCategory(String category, CategoryIcon icon) {
        if (category != null && !category.trim().isEmpty() && !allCategories.contains(category)) {
            allCategories.add(category);
            categoryIcons.put(category, icon);
            notifyListeners();
        }
    }
    
    public CategoryIcon getCategoryIcon(String category) {
        return categoryIcons.getOrDefault(category, new CategoryIcon(IconType.OTHER, new Color(149, 165, 166)));
    }
    
    public void generateIconForCategory(String category) {
        if (!categoryIcons.containsKey(category)) {
            // Generate a random color based on the category name's hash code
            int hash = category.hashCode();
            float hue = (hash % 360) / 360.0f;
            Color color = Color.getHSBColor(hue, 0.7f, 0.9f);
            
            // Choose an icon type based on the category name
            IconType type = determineIconTypeFromName(category);
            
            // Create and store the icon
            categoryIcons.put(category, new CategoryIcon(type, color));
        }
    }
    
    private IconType determineIconTypeFromName(String category) {
        String lowerCase = category.toLowerCase();
        
        if (lowerCase.contains("house") || lowerCase.contains("home") || lowerCase.contains("rent") || lowerCase.contains("mortgage")) {
            return IconType.HOME;
        } else if (lowerCase.contains("food") || lowerCase.contains("grocery") || lowerCase.contains("meal") || lowerCase.contains("restaurant")) {
            return IconType.FOOD;
        } else if (lowerCase.contains("car") || lowerCase.contains("transport") || lowerCase.contains("gas") || lowerCase.contains("fuel")) {
            return IconType.CAR;
        } else if (lowerCase.contains("entertain") || lowerCase.contains("movie") || lowerCase.contains("game")) {
            return IconType.ENTERTAINMENT;
        } else if (lowerCase.contains("health") || lowerCase.contains("doctor") || lowerCase.contains("medical")) {
            return IconType.HEALTH;
        } else if (lowerCase.contains("edu") || lowerCase.contains("school") || lowerCase.contains("college") || lowerCase.contains("tuition")) {
            return IconType.EDUCATION;
        } else if (lowerCase.contains("save") || lowerCase.contains("invest")) {
            return IconType.SAVINGS;
        } else if (lowerCase.contains("income") || lowerCase.contains("salary") || lowerCase.contains("wage")) {
            return IconType.INCOME;
        } else if (lowerCase.contains("shop") || lowerCase.contains("buy") || lowerCase.contains("purchase")) {
            return IconType.SHOPPING;
        } else if (lowerCase.contains("bill") || lowerCase.contains("utility")) {
            return IconType.BILL;
        } else if (lowerCase.contains("gift") || lowerCase.contains("present")) {
            return IconType.GIFT;
        } else if (lowerCase.contains("travel") || lowerCase.contains("vacation") || lowerCase.contains("trip")) {
            return IconType.TRAVEL;
        } else if (lowerCase.contains("fitness") || lowerCase.contains("gym") || lowerCase.contains("sport")) {
            return IconType.FITNESS;
        } else if (lowerCase.contains("tech") || lowerCase.contains("computer") || lowerCase.contains("phone") || lowerCase.contains("device")) {
            return IconType.TECHNOLOGY;
        } else {
            return IconType.CUSTOM;
        }
    }

    private void notifyListeners() {
        List<String> sortedCategories = getAllCategories();
        for (CategoryChangeListener listener : listeners) {
            listener.onCategoriesChanged(sortedCategories);
        }
    }
}
