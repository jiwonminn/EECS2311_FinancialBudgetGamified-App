package view;
import controller.AnalyticsController;
import controller.TransactionController;
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
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.ui.TextAnchor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.io.File;
import java.io.IOException;

public class AnalyticsUI extends JPanel {
    private AnalyticsController controller;
    private TransactionController transactionController;
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
            transactionController = new TransactionController();
            initializeUI();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error initializing Analytics: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initializeUI() throws SQLException {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createChartPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        // Create and configure analytics labels
        JLabel analyticsLabel = new JLabel("Analytics Dashboard");
        analyticsLabel.setForeground(TEXT_COLOR);
        analyticsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        analyticsLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel analyticsDescriptionLabel = new JLabel("View your spending patterns and financial insights");
        analyticsDescriptionLabel.setForeground(new Color(180, 180, 180));
        analyticsDescriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        analyticsDescriptionLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel analyticsTextPanel = createTextPanel(analyticsLabel, analyticsDescriptionLabel);
        headerPanel.add(analyticsTextPanel, BorderLayout.CENTER);
        headerPanel.add(createImportButtonPanel(), BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTextPanel(JLabel titleLabel, JLabel descriptionLabel) {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BACKGROUND_COLOR);
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(descriptionLabel);
        return textPanel;
    }

    private JPanel createImportButtonPanel() {
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
        return importPanel;
    }

    private JPanel createChartPanel() throws SQLException {
        JPanel chartPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        chartPanel.setBackground(BACKGROUND_COLOR);
        chartPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Initialize chart panels
        savingsChartPanel = createStyledChartPanel(createSavingsChart());
        expensesCategoryChartPanel = createStyledChartPanel(createExpensesByCategoryChart());
        pieChartPanel = createStyledChartPanel(createPieChart());
        barChartPanel = createStyledChartPanel(createBarChart());

        // Add charts to the panel
        chartPanel.add(savingsChartPanel);
        chartPanel.add(expensesCategoryChartPanel);
        chartPanel.add(pieChartPanel);
        chartPanel.add(barChartPanel);

        return chartPanel;
    }

    private ChartPanel createStyledChartPanel(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(400, 300));
        panel.setMinimumSize(new Dimension(300, 200));
        return panel;
    }

    private JFreeChart createBarChart() throws SQLException {
        // Create and populate dataset
        DefaultCategoryDataset dataset = createChartDataset();

        // Create base chart
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

        // Apply styling and customization
        customizeChartAppearance(chart);

        return chart;
    }

    private DefaultCategoryDataset createChartDataset() throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Get and aggregate data
        Map<String, Double> monthlyIncome = aggregateByMonth(controller.getIncomeData());
        Map<String, Double> monthlyExpenses = aggregateByMonth(controller.getExpenseData());

        // Combine all unique months in sorted order
        Set<String> allMonths = new TreeSet<>(monthlyIncome.keySet());
        allMonths.addAll(monthlyExpenses.keySet());

        // Populate dataset
        for (String month : allMonths) {
            dataset.addValue(monthlyIncome.getOrDefault(month, 0.0), "Income", month);
            dataset.addValue(monthlyExpenses.getOrDefault(month, 0.0), "Expenses", month);
        }

        return dataset;
    }

    private void customizeChartAppearance(JFreeChart chart) {
        // Apply dark theme
        applyDarkTheme(chart);

        CategoryPlot plot = chart.getCategoryPlot();

        // Configure renderer
        configureBarRenderer(plot);
        
        // Configure axes
        configureRangeAxis(plot);
        configureDomainAxis(plot);
    }

    private void configureBarRenderer(CategoryPlot plot) {
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        
        // Set bar colors
        renderer.setSeriesPaint(0, CORRECT_COLOR);  // Income - green
        renderer.setSeriesPaint(1, INCORRECT_COLOR); // Expenses - red

        // Configure bar spacing
        renderer.setItemMargin(0.3);

        // Configure value labels
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                "${2}", new DecimalFormat("0.00")));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelPaint(TEXT_COLOR);
    }

    private void configureRangeAxis(CategoryPlot plot) {
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setUpperMargin(0.25);
        rangeAxis.setNumberFormatOverride(new DecimalFormat("$#,##0.00"));
    }

    private void configureDomainAxis(CategoryPlot plot) {
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.4);
    }

    // Create a pie chart for income vs expenses
    private JFreeChart createPieChart() throws SQLException {
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

    private JFreeChart createSavingsChart() throws SQLException {
        Map<LocalDate, Double> savingsData = controller.getSavingsOverTime();
        boolean useMonthly = savingsData.size() > 60;
        
        JFreeChart chart = useMonthly 
                ? createMonthlySavingsChart(savingsData) 
                : createDailySavingsChart(savingsData);
        
        applyChartStyling(chart);
        return chart;
    }

    private JFreeChart createMonthlySavingsChart(Map<LocalDate, Double> savingsData) {
        DefaultCategoryDataset dataset = createMonthlyDataset(savingsData);
        
        JFreeChart chart = ChartFactory.createLineChart(
                "Monthly Savings Trend",
                "",
                "Amount ($)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        
        configureMonthlyRenderer(chart);
        return chart;
    }

    private DefaultCategoryDataset createMonthlyDataset(Map<LocalDate, Double> savingsData) {
        Map<String, Double> monthlySavings = aggregateByMonth(savingsData);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        new TreeSet<>(monthlySavings.entrySet()).forEach(entry -> 
                dataset.addValue(entry.getValue(), "Savings", entry.getKey()));
        
        return dataset;
    }

    private void configureMonthlyRenderer(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        
        renderer.setSeriesPaint(0, CORRECT_COLOR);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                "${2}", new DecimalFormat("0.00")));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelPaint(TEXT_COLOR);
    }

    private JFreeChart createDailySavingsChart(Map<LocalDate, Double> savingsData) {
        TimeSeriesCollection dataset = createDailyDataset(savingsData);
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Daily Savings Trend",
                "Date",
                "Amount ($)",
                dataset,
                false,
                true,
                false
        );
        
        configureDailyRenderer(chart);
        return chart;
    }

    private TimeSeriesCollection createDailyDataset(Map<LocalDate, Double> savingsData) {
        TimeSeries series = new TimeSeries("Savings");
        savingsData.forEach((date, value) -> 
                series.add(new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear()), value));
        
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

    private void configureDailyRenderer(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        renderer.setSeriesPaint(0, CORRECT_COLOR);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);
    }

    private void applyChartStyling(JFreeChart chart) {
        applyDarkTheme(chart);
        
        TextTitle subtitle = new TextTitle(
                "Track your cumulative savings over time",
                new Font("Arial", Font.ITALIC, 12)
        );
        subtitle.setPaint(new Color(180, 180, 180));
        chart.addSubtitle(subtitle);
    }

    private JFreeChart createExpensesByCategoryChart() throws SQLException {
        // Get and process data
        Map<String, Double> expensesByCategory = controller.getExpensesByCategory();
        double totalExpenses = calculateTotalExpenses(expensesByCategory);
        
        // Create chart
        JFreeChart chart = buildDonutChart(expensesByCategory, totalExpenses);
        
        // Apply styling
        customizeDonutChart(chart, expensesByCategory);
        
        return chart;
    }

    private double calculateTotalExpenses(Map<String, Double> expensesByCategory) {
        return expensesByCategory.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private JFreeChart buildDonutChart(Map<String, Double> expensesByCategory, double totalExpenses) {
        DefaultPieDataset<String> dataset = createFilteredDataset(expensesByCategory);
        
        JFreeChart chart = ChartFactory.createPieChart(
                "Expense Categories",
                dataset,
                false,  // Legend
                true,   // Tooltips
                false   // URLs
        );
        
        addTotalExpensesSubtitle(chart, totalExpenses);
        applyDarkTheme(chart);
        
        return chart;
    }

    private DefaultPieDataset<String> createFilteredDataset(Map<String, Double> expensesByCategory) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        expensesByCategory.entrySet().stream()
                .filter(entry -> entry.getValue() > 0.01)
                .forEach(entry -> dataset.setValue(entry.getKey(), entry.getValue()));
        return dataset;
    }

    private void addTotalExpensesSubtitle(JFreeChart chart, double totalExpenses) {
        TextTitle subtitle = new TextTitle(
                String.format("Total Expenses: $%.2f", totalExpenses),
                new Font("Arial", Font.PLAIN, 12)
        );
        subtitle.setPaint(TEXT_COLOR);
        chart.addSubtitle(subtitle);
    }

    private void customizeDonutChart(JFreeChart chart, Map<String, Double> expensesByCategory) {
        PiePlot plot = (PiePlot) chart.getPlot();
        
        configureDonutAppearance(plot);
        applyCategoryColors(plot, expensesByCategory);
        configureLabels(plot);
    }

    private void configureDonutAppearance(PiePlot plot) {
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 11));
        plot.setLabelPaint(TEXT_COLOR);
        plot.setLabelBackgroundPaint(null);
        plot.setLabelOutlinePaint(null);
        plot.setShadowPaint(null);
        plot.setCircular(true);
        plot.setLabelGap(0.02);
        plot.setInteriorGap(0.04);
        
        if (plot instanceof org.jfree.chart.plot.RingPlot) {
            ((org.jfree.chart.plot.RingPlot) plot).setSectionDepth(0.35);
        }
    }

    private void applyCategoryColors(PiePlot plot, Map<String, Double> expensesByCategory) {
        int colorIndex = 0;
        for (String category : expensesByCategory.keySet()) {
            if (expensesByCategory.get(category) > 0.01) {
                plot.setSectionPaint(category, EXPENSE_CATEGORY_COLORS[colorIndex % EXPENSE_CATEGORY_COLORS.length]);
                colorIndex++;
            }
        }
    }

    private void configureLabels(PiePlot plot) {
        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                "{0}: {2} (${1})", 
                new DecimalFormat("0.00"), 
                new DecimalFormat("0%")
        );
        plot.setLabelGenerator(labelGenerator);
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

    // Handle CSV import
    private void handleCsvImport() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File fileToImport = fileChooser.getSelectedFile();
            try {
                // Use the TransactionController to import transactions from the CSV file
                transactionController.importTransactionsFromCsv(fileToImport);
                JOptionPane.showMessageDialog(this, "Transactions imported successfully!");
                refreshCharts(); // Refresh the charts after import
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error importing transactions: " + e.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshCharts() throws SQLException {
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

