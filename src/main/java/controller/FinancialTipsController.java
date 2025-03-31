package controller;

import model.FinancialTip;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing financial tips.
 * Handles loading and filtering tips by category.
 */
public class FinancialTipsController {
    private List<FinancialTip> tips;

    /**
     * Creates a new FinancialTipsController with default tips.
     */
    public FinancialTipsController() {
        this.tips = new ArrayList<>();
        loadDefaultTips();
    }

    /**
     * Gets all available tips.
     *
     * @return List of all financial tips
     */
    public List<FinancialTip> getAllTips() {
        return new ArrayList<>(tips);
    }

    /**
     * Gets tips filtered by category.
     *
     * @param category The category to filter by
     * @return List of tips from the specified category
     */
    public List<FinancialTip> getTipsByCategory(String category) {
        return tips.stream()
                .filter(tip -> tip.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of all available categories.
     *
     * @return List of unique categories
     */
    public List<String> getCategories() {
        return tips.stream()
                .map(FinancialTip::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Loads a predefined set of financial tips.
     */
    private void loadDefaultTips() {
        // Budgeting tips
        tips.add(new FinancialTip(
            "Create a Monthly Budget",
            "Start by listing all your income sources and expenses. Allocate specific amounts to different categories and track your spending regularly.",
            "Budgeting",
            "2024-03-20"
        ));

        tips.add(new FinancialTip(
            "Use the 50/30/20 Rule",
            "Allocate 50% of your income to needs, 30% to wants, and 20% to savings and debt repayment.",
            "Budgeting",
            "2024-03-20"
        ));

        // Saving tips
        tips.add(new FinancialTip(
            "Build an Emergency Fund",
            "Aim to save 3-6 months of living expenses in an easily accessible account for unexpected situations.",
            "Saving",
            "2024-03-20"
        ));

        tips.add(new FinancialTip(
            "Automate Your Savings",
            "Set up automatic transfers to your savings account on payday to make saving a priority.",
            "Saving",
            "2024-03-20"
        ));

        // Investing tips
        tips.add(new FinancialTip(
            "Start Investing Early",
            "The earlier you start investing, the more time your money has to grow through compound interest.",
            "Investing",
            "2024-03-20"
        ));

        tips.add(new FinancialTip(
            "Diversify Your Portfolio",
            "Spread your investments across different asset classes to reduce risk.",
            "Investing",
            "2024-03-20"
        ));

        // Credit tips
        tips.add(new FinancialTip(
            "Maintain Good Credit",
            "Pay your bills on time, keep your credit utilization low, and regularly check your credit report.",
            "Credit",
            "2024-03-20"
        ));

        tips.add(new FinancialTip(
            "Understand Credit Cards",
            "Use credit cards responsibly, pay in full each month, and avoid carrying a balance.",
            "Credit",
            "2024-03-20"
        ));

        // Financial Planning tips
        tips.add(new FinancialTip(
            "Set Financial Goals",
            "Define clear, specific financial goals with timelines to stay motivated and track progress.",
            "Financial Planning",
            "2024-03-20"
        ));

        tips.add(new FinancialTip(
            "Plan for Retirement",
            "Start saving for retirement early and take advantage of employer-sponsored retirement plans.",
            "Financial Planning",
            "2024-03-20"
        ));
    }
} 