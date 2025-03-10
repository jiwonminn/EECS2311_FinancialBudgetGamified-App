package view;
import controller.AnalyticsController;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.format.DateTimeFormatter;

public class AnalyticsUI extends JPanel {
    private AnalyticsController controller;
    private int userId;
    private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    private final Color PANEL_COLOR = new Color(40, 24, 69);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color ACCENT_COLOR = new Color(128, 90, 213);
    private final Color CORRECT_COLOR = new Color(39, 174, 96);
    private final Color INCORRECT_COLOR = new Color(215, 38, 61);
    private final Color FIELD_BACKGROUND = new Color(50, 35, 80);
    private final Color FIELD_BORDER = new Color(70, 50, 110);

    private ChartPanel barChartPanel;
    private ChartPanel pieChartPanel;
    private ChartPanel savingsChartPanel;
    private ChartPanel expensesCategoryChartPanel;

    public AnalyticsUI(int userId) {
        this.userId = userId;
        controller = new AnalyticsController(userId);
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

        // Add import button panel above the charts
        JPanel importPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        importPanel.setBackground(BACKGROUND_COLOR);
        importPanel.setBorder(new EmptyBorder(0, 20, 10, 20));

        JButton importButton = new JButton("Import CSV");
        importButton.setBackground(ACCENT_COLOR);
        importButton.setForeground(TEXT_COLOR);
        importButton.setFocusPainted(false);
        importButton.setOpaque(true);
        importButton.setBorderPainted(false);
        importButton.addActionListener(e -> handleCsvImport());

        importPanel.add(importButton);
        add(importPanel, BorderLayout.NORTH);

        // Create a panel for the charts
        JPanel analyticsChartPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        analyticsChartPanel.setBackground(BACKGROUND_COLOR);
        analyticsChartPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create and store chart panels
        barChartPanel = new ChartPanel(createBarChart());
        pieChartPanel = new ChartPanel(createPieChart());
        savingsChartPanel = new ChartPanel(createSavingsChart());
        expensesCategoryChartPanel = new ChartPanel(createExpensesByCategoryChart());

        // Add charts to the panel
        analyticsChartPanel.add(barChartPanel);
        analyticsChartPanel.add(pieChartPanel);
        analyticsChartPanel.add(savingsChartPanel);
        analyticsChartPanel.add(expensesCategoryChartPanel);

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

    private void handleCsvImport() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                String line;
                boolean firstLine = true;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                while ((line = br.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue; // Skip header row
                    }

                    String[] values = line.split(",");
                    if (values.length >= 5) {
                        // Remove any quotes and trim whitespace
                        String dateStr = values[0].trim().replace("\"", "").replace("'", "");
                        String description = values[1].trim().replace("\"", "").replace("'", "");
                        String expense = values[2].trim().replace("\"", "").replace("'", "");
                        String income = values[3].trim().replace("\"", "").replace("'", "");

                        try {
                            LocalDate date = LocalDate.parse(dateStr, formatter);

                            if (!expense.isEmpty()) {
                                // Add expense transaction
                                controller.addTransaction(date, Double.parseDouble(expense),
                                        description, "expense", "Uncategorized");
                            } else if (!income.isEmpty()) {
                                // Add income transaction
                                controller.addTransaction(date, Double.parseDouble(income),
                                        description, "income", "Uncategorized");
                            }
                        } catch (Exception e) {
                            System.out.println("Error parsing date: " + dateStr);
                            continue; // Skip this row and continue with next
                        }
                    }
                }

                // Show success message
                JOptionPane.showMessageDialog(this,
                        "CSV file imported successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Refresh the charts
                refreshCharts();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error importing CSV file: " + ex.getMessage() +
                                "\nMake sure your CSV format is: date,description,expense,income,balance" +
                                "\nDate should be in YYYY-MM-DD format",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshCharts() {
        // Update each chart with new data
        barChartPanel.setChart(createBarChart());
        pieChartPanel.setChart(createPieChart());
        savingsChartPanel.setChart(createSavingsChart());
        expensesCategoryChartPanel.setChart(createExpensesByCategoryChart());

        // Refresh the display
        revalidate();
        repaint();
    }

}
