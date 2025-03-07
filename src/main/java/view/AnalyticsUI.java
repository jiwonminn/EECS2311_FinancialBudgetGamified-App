package view;

import controller.AnalyticsController;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AnalyticsUI extends JFrame {
    private AnalyticsController controller;

    public AnalyticsUI() {
        controller = new AnalyticsController();
        initializeUI();
    }

    private void initializeUI() {
        // Set up the JFrame
        setTitle("Transaction Charts and Savings Tracker");
        setSize(1200, 1000); // Increased size to accommodate more charts
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1)); // Four rows, one column

        // Create charts
        JFreeChart barChart = createBarChart();
        JFreeChart pieChart = createPieChart();
        JFreeChart savingsChart = createSavingsChart();
        JFreeChart expensesByCategoryChart = createExpensesByCategoryChart();

        // Add charts to the JFrame
        add(new ChartPanel(barChart));
        add(new ChartPanel(pieChart));
        add(new ChartPanel(savingsChart));
        add(new ChartPanel(expensesByCategoryChart));

        // Add a label to display current savings
        JLabel savingsLabel = new JLabel("Current Savings: $" + String.format("%.2f", controller.getCurrentSavings()));
        savingsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        savingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(savingsLabel);

        // Display the JFrame
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    // Create a BarChart
    private JFreeChart createBarChart() {
    	// Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Get income and expense data
        Map<LocalDate, Double> incomeData = controller.getIncomeData();
        Map<LocalDate, Double> expenseData = controller.getExpenseData();

        // Combine all unique dates from income and expense data
        Set<LocalDate> allDates = new TreeSet<>(incomeData.keySet()); // TreeSet sorts dates automatically
        allDates.addAll(expenseData.keySet());

        // Add income and expense data to the dataset in chronological order
        for (LocalDate date : allDates) {
            double income = incomeData.getOrDefault(date, 0.0);
            double expense = expenseData.getOrDefault(date, 0.0);
            dataset.addValue(income, "Income", date.toString());
            dataset.addValue(expense, "Expense", date.toString());
        }

        // Create BarChart
        return ChartFactory.createBarChart(
                "Income vs Expenses Over Time", // Chart title
                "Date",                          // X-axis label
                "Amount",                        // Y-axis label
                dataset,                         // Dataset
                PlotOrientation.VERTICAL,        // Orientation
                true,                           // Include legend
                true,
                false
        );
    }

    // Create a PieChart
    private JFreeChart createPieChart() {
        // Create dataset
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Add data to dataset
        dataset.setValue("Income", controller.getTotalIncome());
        dataset.setValue("Expense", controller.getTotalExpense());

        // Create PieChart
        return ChartFactory.createPieChart(
                "Income vs Expenses", // Chart title
                dataset,             // Dataset
                true,               // Include legend
                true,
                false
        );
    }

    // Create a LineChart for savings over time
    private JFreeChart createSavingsChart() {
    	// Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Get savings data
        Map<LocalDate, Double> savingsData = controller.getSavingsOverTime();

        // Sort savings data by date (from smallest to largest)
        savingsData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Sort by date (LocalDate is Comparable)
                .forEach(entry -> dataset.addValue(entry.getValue(), "Savings", entry.getKey().toString()));

        // Create LineChart
        return ChartFactory.createLineChart(
                "Savings Over Time", // Chart title
                "Date",              // X-axis label
                "Amount",            // Y-axis label
                dataset,             // Dataset
                PlotOrientation.VERTICAL, // Orientation
                true,               // Include legend
                true,
                false
        );
    }

    // Create a PieChart for expenses by category
    private JFreeChart createExpensesByCategoryChart() {
        // Create dataset
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Get expenses by category
        Map<String, Double> expensesByCategory = controller.getExpensesByCategory();

        // Add data to dataset
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // Create PieChart
        return ChartFactory.createPieChart(
                "Expenses by Category", // Chart title
                dataset,               // Dataset
                true,                 // Include legend
                true,
                false
        );
    }
    public static void main(String[] args) {
        // Run the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new AnalyticsUI());
    }
}