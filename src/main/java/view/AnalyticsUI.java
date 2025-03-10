package view;
import controller.AnalyticsController;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

    // Chart specific colors
    private final Color[] EXPENSE_CATEGORY_COLORS = {
            new Color(215, 38, 61),    // Red
            new Color(252, 109, 36),   // Orange
            new Color(250, 176, 5),    // Yellow
            new Color(59, 145, 220),   // Blue
            new Color(39, 174, 96),    // Green
            new Color(142, 68, 173),   // Purple
            new Color(22, 160, 133),   // Teal
            new Color(127, 140, 141),  // Gray
            new Color(241, 196, 15),   // Gold
            new Color(231, 76, 60)     // Light Red
    };

    private ChartPanel barChartPanel;
    private ChartPanel pieChartPanel;
    private ChartPanel savingsChartPanel;
    private ChartPanel expensesCategoryChartPanel;

    public AnalyticsUI(int userId) {
        this.userId = userId;
        try {
            controller = new AnalyticsController(userId);
            initializeUI();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error initializing Analytics: " + e.getMessage(), 
                "Initialization Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        // Set up the main panel
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Add header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel analyticsLabel = new JLabel("Analytics Dashboard");
        analyticsLabel.setForeground(TEXT_COLOR);
        analyticsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        analyticsLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel analyticsDescriptionLabel = new JLabel("View your spending patterns and financial insights");
        analyticsDescriptionLabel.setForeground(new Color(180, 180, 180));
        analyticsDescriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        analyticsDescriptionLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel analyticsTextPanel = new JPanel();
        analyticsTextPanel.setLayout(new BoxLayout(analyticsTextPanel, BoxLayout.Y_AXIS));
        analyticsTextPanel.setBackground(BACKGROUND_COLOR);
        analyticsTextPanel.add(analyticsLabel);
        analyticsTextPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        analyticsTextPanel.add(analyticsDescriptionLabel);

        headerPanel.add(analyticsTextPanel, BorderLayout.CENTER);

        // Add import button to the header
        JButton importButton = new JButton("Import CSV");
        importButton.setBackground(ACCENT_COLOR);
        importButton.setForeground(TEXT_COLOR);
        importButton.setFocusPainted(false);
        importButton.setOpaque(true);
        importButton.setBorderPainted(false);
        importButton.addActionListener(e -> handleCsvImport());
        
        JPanel importPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        importPanel.setBackground(BACKGROUND_COLOR);
        importPanel.add(importButton);
        
        headerPanel.add(importPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Create a panel for the charts
        JPanel analyticsChartPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        analyticsChartPanel.setBackground(BACKGROUND_COLOR);
        analyticsChartPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Create and store chart panels
        savingsChartPanel = new ChartPanel(createSavingsChart());
        expensesCategoryChartPanel = new ChartPanel(createExpensesByCategoryChart());
        pieChartPanel = new ChartPanel(createPieChart());
        barChartPanel = new ChartPanel(createBarChart());

        // Customize chart panels
        for (ChartPanel panel : new ChartPanel[]{savingsChartPanel, expensesCategoryChartPanel, pieChartPanel, barChartPanel}) {
            panel.setBackground(PANEL_COLOR);
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel.setPreferredSize(new Dimension(400, 300));
            panel.setMinimumSize(new Dimension(300, 200));
        }

        // Add charts to the panel
        analyticsChartPanel.add(savingsChartPanel);
        analyticsChartPanel.add(expensesCategoryChartPanel);
        analyticsChartPanel.add(pieChartPanel);
        analyticsChartPanel.add(barChartPanel);

        // Add the chart panel to the main panel
        add(analyticsChartPanel, BorderLayout.CENTER);
    }

    // Create a monthly income vs expenses bar chart
    private JFreeChart createBarChart() {
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Get income and expense data
        Map<LocalDate, Double> incomeData = controller.getIncomeData();
        Map<LocalDate, Double> expenseData = controller.getExpenseData();

        // Aggregate by month
        Map<String, Double> monthlyIncome = aggregateByMonth(incomeData);
        Map<String, Double> monthlyExpenses = aggregateByMonth(expenseData);

        // Combine all unique months
        Set<String> allMonths = new TreeSet<>(monthlyIncome.keySet());
        allMonths.addAll(monthlyExpenses.keySet());

        // Add income and expense data to the dataset in chronological order
        for (String month : allMonths) {
            double income = monthlyIncome.getOrDefault(month, 0.0);
            double expense = monthlyExpenses.getOrDefault(month, 0.0);
            dataset.addValue(income, "Income", month);
            dataset.addValue(expense, "Expenses", month);
        }

        // Create BarChart
        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Income vs Expenses", // Chart title
                "",                          // X-axis label
                "Amount ($)",                // Y-axis label
                dataset,                     // Dataset
                PlotOrientation.VERTICAL,    // Orientation
                true,                        // Include legend
                true,                        // Tooltips
                false                        // URLs
        );

        // Apply dark theme
        applyDarkTheme(chart);

        // Customize the plot
        CategoryPlot plot = chart.getCategoryPlot();
        
        // Customize renderer
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, CORRECT_COLOR);  // Income - green
        renderer.setSeriesPaint(1, INCORRECT_COLOR); // Expenses - red
        
        // Add dollar value labels on top of the bars
        renderer.setDefaultItemLabelGenerator(new org.jfree.chart.labels.StandardCategoryItemLabelGenerator(
                "${2}", new DecimalFormat("0.00")));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelPaint(TEXT_COLOR);
        
        return chart;
    }

    // Create a pie chart for income vs expenses
    private JFreeChart createPieChart() {
        // Create dataset
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        // Get totals
        double totalIncome = controller.getTotalIncome();
        double totalExpense = controller.getTotalExpense();

        // Add data to dataset
        dataset.setValue("Income", totalIncome);
        dataset.setValue("Expenses", totalExpense);

        // Create PieChart
        JFreeChart chart = ChartFactory.createPieChart(
                "Income vs Expenses Distribution", // Chart title
                dataset,                           // Dataset
                false,                             // Legend
                true,                              // Tooltips
                false                              // URLs
        );
        
        // Add subtitle with total amounts
        TextTitle subtitle = new TextTitle(
                String.format("Income: $%.2f | Expenses: $%.2f", totalIncome, totalExpense),
                new Font("Arial", Font.PLAIN, 12)
        );
        subtitle.setPaint(TEXT_COLOR);
        chart.addSubtitle(subtitle);

        // Apply dark theme
        applyDarkTheme(chart);

        // Customize the plot
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Income", CORRECT_COLOR);
        plot.setSectionPaint("Expenses", INCORRECT_COLOR);
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.setLabelPaint(TEXT_COLOR);
        plot.setLabelBackgroundPaint(null);
        plot.setLabelOutlinePaint(null);
        plot.setShadowPaint(null);
        
        // Add percentage labels
        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                "{0}: {2}", new DecimalFormat("0.00"), new DecimalFormat("0%"));
        plot.setLabelGenerator(labelGenerator);

        return chart;
    }

    // Create a line chart for savings over time
    private JFreeChart createSavingsChart() {
        // Get savings data
        Map<LocalDate, Double> savingsData = controller.getSavingsOverTime();
        boolean useMonthly = savingsData.size() > 60; // Use monthly aggregation if we have lots of data points
        
        JFreeChart chart;
        
        if (useMonthly) {
            // Aggregate by month for clarity with lots of data
            Map<String, Double> monthlySavings = aggregateByMonth(savingsData);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            
            // Add to dataset
            for (Map.Entry<String, Double> entry : new TreeSet<>(monthlySavings.entrySet())) {
                dataset.addValue(entry.getValue(), "Savings", entry.getKey());
            }
            
            // Create line chart
            chart = ChartFactory.createLineChart(
                    "Monthly Savings Trend",    // Chart title
                    "",                         // X-axis label
                    "Amount ($)",               // Y-axis label
                    dataset,                    // Dataset
                    PlotOrientation.VERTICAL,   // Orientation
                    false,                      // Include legend
                    true,                       // Tooltips
                    false                       // URLs
            );
            
            // Customize renderer
            CategoryPlot plot = chart.getCategoryPlot();
            LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, CORRECT_COLOR);
            renderer.setSeriesStroke(0, new BasicStroke(2.5f));
            renderer.setDefaultShapesVisible(true);
            renderer.setDefaultItemLabelGenerator(new org.jfree.chart.labels.StandardCategoryItemLabelGenerator(
                    "${2}", new DecimalFormat("0.00")));
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelPaint(TEXT_COLOR);
        } else {
            // Create time series dataset for daily data
            TimeSeries series = new TimeSeries("Savings");
            
            // Add data points
            for (Map.Entry<LocalDate, Double> entry : savingsData.entrySet()) {
                LocalDate date = entry.getKey();
                series.add(new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear()), entry.getValue());
            }
            
            TimeSeriesCollection dataset = new TimeSeriesCollection();
            dataset.addSeries(series);
            
            // Create chart
            chart = ChartFactory.createTimeSeriesChart(
                    "Daily Savings Trend",      // Chart title
                    "Date",                     // X-axis label
                    "Amount ($)",               // Y-axis label
                    dataset,                    // Dataset
                    false,                      // Include legend
                    true,                       // Tooltips
                    false                       // URLs
            );
            
            // Customize plot
            XYPlot plot = chart.getXYPlot();
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesPaint(0, CORRECT_COLOR);
            renderer.setSeriesStroke(0, new BasicStroke(2.5f));
            renderer.setSeriesShapesVisible(0, true);
            plot.setRenderer(renderer);
        }
        
        // Apply dark theme and add subtitle
        applyDarkTheme(chart);
        TextTitle subtitle = new TextTitle(
                "Track your cumulative savings over time",
                new Font("Arial", Font.ITALIC, 12)
        );
        subtitle.setPaint(new Color(180, 180, 180));
        chart.addSubtitle(subtitle);
        
        return chart;
    }

    // Create a donut chart for expenses by category
    private JFreeChart createExpensesByCategoryChart() {
        // Create dataset
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        // Get expenses by category
        Map<String, Double> expensesByCategory = controller.getExpensesByCategory();
        double totalExpenses = expensesByCategory.values().stream().mapToDouble(Double::doubleValue).sum();

        // Add data to dataset (filter out categories with very small values)
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            if (entry.getValue() > 0.01) {  // Filter out very small values
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }

        // Create PieChart (we'll customize it to be a donut chart)
        JFreeChart chart = ChartFactory.createPieChart(
                "Expense Categories",        // Chart title
                dataset,                     // Dataset
                false,                       // Legend 
                true,                        // Tooltips
                false                        // URLs
        );

        // Add subtitle with total expenses
        TextTitle subtitle = new TextTitle(
                String.format("Total Expenses: $%.2f", totalExpenses),
                new Font("Arial", Font.PLAIN, 12)
        );
        subtitle.setPaint(TEXT_COLOR);
        chart.addSubtitle(subtitle);

        // Apply dark theme
        applyDarkTheme(chart);

        // Customize the plot to create a donut chart effect
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 11));
        plot.setLabelPaint(TEXT_COLOR);
        plot.setLabelBackgroundPaint(null);
        plot.setLabelOutlinePaint(null);
        plot.setShadowPaint(null);
        plot.setCircular(true);
        plot.setLabelGap(0.02);
        plot.setInteriorGap(0.04);
        
        // JFreeChart 1.5+ uses this instead of setSectionDepth for donut effect
        if (plot instanceof org.jfree.chart.plot.RingPlot) {
            ((org.jfree.chart.plot.RingPlot) plot).setSectionDepth(0.35);
        }
        
        // Set custom colors for each category
        int colorIndex = 0;
        for (Object key : dataset.getKeys()) {
            // Cast the key to Comparable to match the expected type
            plot.setSectionPaint((Comparable<?>) key, EXPENSE_CATEGORY_COLORS[colorIndex % EXPENSE_CATEGORY_COLORS.length]);
            colorIndex++;
        }
        
        // Add percentage and dollar amount labels
        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                "{0}: {2} (${1})", new DecimalFormat("0.00"), new DecimalFormat("0%"));
        plot.setLabelGenerator(labelGenerator);

        return chart;
    }

    // Apply dark theme to all charts
    private void applyDarkTheme(JFreeChart chart) {
        // Background
        chart.setBackgroundPaint(PANEL_COLOR);
        
        // Title
        chart.getTitle().setPaint(TEXT_COLOR);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        
        // Plot
        if (chart.getPlot() instanceof PiePlot) {
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(PANEL_COLOR);
            plot.setOutlinePaint(PANEL_COLOR);
            plot.setLabelOutlinePaint(null);
            plot.setLabelShadowPaint(null);
        } else if (chart.getPlot() instanceof CategoryPlot) {
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(PANEL_COLOR);
            plot.setDomainGridlinePaint(new Color(70, 50, 110));
            plot.setRangeGridlinePaint(new Color(70, 50, 110));
            plot.setOutlinePaint(PANEL_COLOR);
            
            // Axes
            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setLabelPaint(TEXT_COLOR);
            domainAxis.setTickLabelPaint(TEXT_COLOR);
            domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 11));
            
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setLabelPaint(TEXT_COLOR);
            rangeAxis.setTickLabelPaint(TEXT_COLOR);
            rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 11));
            rangeAxis.setNumberFormatOverride(new DecimalFormat("$#,##0.00"));
        } else if (chart.getPlot() instanceof XYPlot) {
            XYPlot plot = (XYPlot) chart.getPlot();
            plot.setBackgroundPaint(PANEL_COLOR);
            plot.setDomainGridlinePaint(new Color(70, 50, 110));
            plot.setRangeGridlinePaint(new Color(70, 50, 110));
            plot.setOutlinePaint(PANEL_COLOR);
            
            // Axes
            plot.getDomainAxis().setLabelPaint(TEXT_COLOR);
            plot.getDomainAxis().setTickLabelPaint(TEXT_COLOR);
            plot.getDomainAxis().setTickLabelFont(new Font("Arial", Font.PLAIN, 11));
            
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setLabelPaint(TEXT_COLOR);
            rangeAxis.setTickLabelPaint(TEXT_COLOR);
            rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 11));
            rangeAxis.setNumberFormatOverride(new DecimalFormat("$#,##0.00"));
        }
        
        // Legend
        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(PANEL_COLOR);
            chart.getLegend().setItemPaint(TEXT_COLOR);
            chart.getLegend().setBorder(0, 0, 0, 0);
            chart.getLegend().setPosition(RectangleEdge.BOTTOM);
        }
    }

    // Helper method to aggregate data by month
    private Map<String, Double> aggregateByMonth(Map<LocalDate, Double> data) {
        Map<String, Double> monthlyData = new HashMap<>();
        
        for (Map.Entry<LocalDate, Double> entry : data.entrySet()) {
            LocalDate date = entry.getKey();
            String monthYear = date.getMonth().toString() + " " + date.getYear();
            
            // Sum values for the same month
            monthlyData.put(monthYear, monthlyData.getOrDefault(monthYear, 0.0) + entry.getValue());
        }
        
        return monthlyData;
    }

    // CSV import functionality from the new version
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
        savingsChartPanel.setChart(createSavingsChart());
        expensesCategoryChartPanel.setChart(createExpensesByCategoryChart());
        pieChartPanel.setChart(createPieChart());
        barChartPanel.setChart(createBarChart());

        // Refresh the display
        revalidate();
        repaint();
    }
}
