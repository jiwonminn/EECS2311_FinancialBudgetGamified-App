package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
 import org.jfree.data.category.DefaultCategoryDataset;
 import org.jfree.data.general.DefaultPieDataset;

import controller.AnalyticsController;

import javax.swing.*;
 import javax.swing.border.EmptyBorder;
 
 import java.awt.*;
 import java.time.LocalDate;
 import java.util.Map;
 import java.util.Set;
 import java.util.TreeSet;
 
 public class AnalyticsUI extends JPanel {
     private AnalyticsController controller;
     private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
     private final Color PANEL_COLOR = new Color(40, 24, 69);
     private final Color TEXT_COLOR = new Color(255, 255, 255);
     private final Color ACCENT_COLOR = new Color(128, 90, 213);
     private final Color CORRECT_COLOR = new Color(39, 174, 96);
     private final Color INCORRECT_COLOR = new Color(215, 38, 61);
     private final Color FIELD_BACKGROUND = new Color(50, 35, 80);
     private final Color FIELD_BORDER = new Color(70, 50, 110);
        
     
     public AnalyticsUI() {
         controller = new AnalyticsController();
         initializeUI();
     }
 
     private void initializeUI() {
     	 // Set up the main panel
         setLayout(new BorderLayout());
         setBackground(BACKGROUND_COLOR);
 
         // Add header panel
         JPanel analyticsPlaceholderPanel = new JPanel(new BorderLayout());
         analyticsPlaceholderPanel.setBackground(BACKGROUND_COLOR);
         analyticsPlaceholderPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
 
         JLabel analyticsLabel = new JLabel("Analytics Dashboard");
         analyticsLabel.setForeground(TEXT_COLOR);
         analyticsLabel.setFont(new Font("Arial", Font.BOLD, 24));
         analyticsLabel.setHorizontalAlignment(JLabel.CENTER);
 
         JLabel analyticsDescriptionLabel = new JLabel("View your spending patterns and financial insights.");
         analyticsDescriptionLabel.setForeground(new Color(180, 180, 180));
         analyticsDescriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
         analyticsDescriptionLabel.setHorizontalAlignment(JLabel.CENTER);
 
         JPanel analyticsTextPanel = new JPanel();
         analyticsTextPanel.setLayout(new BoxLayout(analyticsTextPanel, BoxLayout.Y_AXIS));
         analyticsTextPanel.setBackground(BACKGROUND_COLOR);
         analyticsTextPanel.add(analyticsLabel);
         analyticsTextPanel.add(Box.createRigidArea(new Dimension(0, 20)));
         analyticsTextPanel.add(analyticsDescriptionLabel);
 
         analyticsPlaceholderPanel.add(analyticsTextPanel, BorderLayout.CENTER);
         add(analyticsPlaceholderPanel, BorderLayout.NORTH);
 
         // Create a panel for the charts
         JPanel analyticsChartPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // 2 rows, 2 columns, with gaps
         analyticsChartPanel.setBackground(BACKGROUND_COLOR);
         analyticsChartPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
 
         // Create and add charts to the panel
         JFreeChart barChart = createBarChart();
         JFreeChart pieChart = createPieChart();
         JFreeChart savingsChart = createSavingsChart();
         JFreeChart expensesByCategoryChart = createExpensesByCategoryChart();
 
         analyticsChartPanel.add(new ChartPanel(barChart));
         analyticsChartPanel.add(new ChartPanel(pieChart));
         analyticsChartPanel.add(new ChartPanel(savingsChart));
         analyticsChartPanel.add(new ChartPanel(expensesByCategoryChart));
 
         // Add the chart panel to the main panel
         add(analyticsChartPanel, BorderLayout.CENTER);
     
 //     // total savings add later
 //        JLabel savingsLabel = new JLabel("Current Savings: $" + String.format("%.2f", controller.getCurrentSavings()));
 //        savingsLabel.setFont(new Font("Arial", Font.BOLD, 16));
 //        savingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
 //        add(savingsLabel);
     
     
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
 }