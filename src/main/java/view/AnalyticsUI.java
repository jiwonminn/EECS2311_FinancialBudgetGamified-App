package view;

import controller.AnalyticsController;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
<<<<<<< HEAD
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AnalyticsUI extends JPanel {
    // Define colors to match CalendarUI
    private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    private final Color PANEL_COLOR = new Color(40, 24, 69);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color ACCENT_COLOR = new Color(128, 90, 213);
    private final Color EXPENSE_COLOR = new Color(215, 38, 61);
    private final Color INCOME_COLOR = new Color(39, 174, 96);
    private final Color FIELD_BACKGROUND = new Color(50, 35, 80);
    private final Color FIELD_BORDER = new Color(70, 50, 110);

    private AnalyticsController controller;
    private JPanel chartPanel;
    private JLabel budgetSummaryLabel;
    private ChartPanel pieChartPanel;
    private ChartPanel barChartPanel;
    private int userId;

    public AnalyticsUI(int userId) {
        this.userId = userId;
        this.controller = new AnalyticsController();
        this.controller.setUserId(userId);
        setupUI();
        updateCharts();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Analytics Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel);

        // Import Panel
        JPanel importPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        importPanel.setBackground(BACKGROUND_COLOR);
        JButton importButton = new JButton("Import Transactions");
        importButton.setBackground(ACCENT_COLOR);
        importButton.setForeground(TEXT_COLOR);
        importButton.setFocusPainted(false);
        importButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        importButton.setFont(new Font("Arial", Font.BOLD, 12));
        importButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        importButton.addActionListener(e -> handleCSVImport());

        JLabel importLabel = new JLabel("Import your transactions from a CSV file");
        importLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        importLabel.setForeground(TEXT_COLOR);
        importPanel.add(importButton);
        importPanel.add(Box.createHorizontalStrut(10));
        importPanel.add(importLabel);

        // Budget Summary Panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER),
                new EmptyBorder(15, 15, 15, 15)
        ));
        summaryPanel.setBackground(PANEL_COLOR);

        JLabel summaryTitle = new JLabel("Budget Overview");
        summaryTitle.setFont(new Font("Arial", Font.BOLD, 18));
        summaryTitle.setForeground(TEXT_COLOR);
        summaryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        budgetSummaryLabel = new JLabel();
        budgetSummaryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        budgetSummaryLabel.setForeground(TEXT_COLOR);
        budgetSummaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        summaryPanel.add(summaryTitle);
        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(budgetSummaryLabel);

        // Charts Panel
        chartPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartPanel.setBackground(PANEL_COLOR);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Top Panel containing title and import
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(titlePanel);
        topPanel.add(importPanel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(summaryPanel);

        add(topPanel, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
    }

    private void handleCSVImport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Transactions");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                controller.importCSVFile(selectedFile);
                updateCharts();

                JOptionPane.showMessageDialog(this,
                        "Transactions imported successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + e.getMessage(),
                        "Import Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateBudgetSummary() {
        double totalBudget = controller.getTotalBudget();
        double totalSpent = controller.getTotalSpent();
        double remainingBudget = controller.getRemainingBudget();
        double spendingPercentage = controller.getSpendingPercentage();

        String summary = String.format(
                "<html><font color='white'>" +
                        "Total Budget: $%,.2f<br>" +
                        "Total Spent: $%,.2f<br>" +
                        "Remaining Budget: $%,.2f<br>" +
                        "Budget Used: %.1f%%</font></html>",
                totalBudget, totalSpent, remainingBudget, spendingPercentage
        );

        budgetSummaryLabel.setText(summary);
    }

    public void updateCharts() {
        // Update Category Pie Chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        Map<String, Double> categorySpending = controller.getCategorySpending();
        for (Map.Entry<String, Double> entry : categorySpending.entrySet()) {
            pieDataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Spending by Category",
                pieDataset,
                true,
                true,
                false
        );

        // Style pie chart
        pieChart.setBackgroundPaint(PANEL_COLOR);
        pieChart.getTitle().setPaint(TEXT_COLOR);
        PiePlot piePlot = (PiePlot) pieChart.getPlot();
        piePlot.setBackgroundPaint(PANEL_COLOR);
        piePlot.setOutlinePaint(FIELD_BORDER);
        piePlot.setLabelBackgroundPaint(PANEL_COLOR);
        piePlot.setLabelOutlinePaint(FIELD_BORDER);
        piePlot.setLabelPaint(TEXT_COLOR);
        pieChart.getLegend().setBackgroundPaint(PANEL_COLOR);
        pieChart.getLegend().setItemPaint(TEXT_COLOR);

        // Update Daily Spending Chart
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        Map<LocalDate, Double> dailySpending = controller.getDailySpending();
        dailySpending.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String date = entry.getKey().format(formatter);
                    barDataset.addValue(entry.getValue(), "Daily Spending", date);
                });

        JFreeChart barChart = ChartFactory.createBarChart(
                "Daily Spending Trends",
                "Date",
                "Amount ($)",
                barDataset
        );

        // Style bar chart
        barChart.setBackgroundPaint(PANEL_COLOR);
        barChart.getTitle().setPaint(TEXT_COLOR);
        CategoryPlot categoryPlot = barChart.getCategoryPlot();
        categoryPlot.setBackgroundPaint(PANEL_COLOR);
        categoryPlot.setOutlinePaint(FIELD_BORDER);
        categoryPlot.getDomainAxis().setLabelPaint(TEXT_COLOR);
        categoryPlot.getDomainAxis().setTickLabelPaint(TEXT_COLOR);
        categoryPlot.getRangeAxis().setLabelPaint(TEXT_COLOR);
        categoryPlot.getRangeAxis().setTickLabelPaint(TEXT_COLOR);
        barChart.getLegend().setBackgroundPaint(PANEL_COLOR);
        barChart.getLegend().setItemPaint(TEXT_COLOR);

        // Create chart panels if they don't exist
        if (pieChartPanel == null) {
            pieChartPanel = new ChartPanel(pieChart);
            barChartPanel = new ChartPanel(barChart);

            // Add borders and titles with dark theme
            TitledBorder pieBorder = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(FIELD_BORDER),
                    "Category Distribution",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 16),
                    TEXT_COLOR
            );
            pieChartPanel.setBorder(pieBorder);

            TitledBorder barBorder = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(FIELD_BORDER),
                    "Spending Timeline",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 16),
                    TEXT_COLOR
            );
            barChartPanel.setBorder(barBorder);

            chartPanel.add(pieChartPanel);
            chartPanel.add(barChartPanel);
        } else {
            pieChartPanel.setChart(pieChart);
            barChartPanel.setChart(barChart);
        }

        // Update Budget Summary
        updateBudgetSummary();

        // Refresh the panel
        revalidate();
        repaint();
    }
=======
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

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

>>>>>>> refs/remotes/origin/Peyton
}