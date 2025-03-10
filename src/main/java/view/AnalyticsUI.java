package view;

import controller.AnalyticsController;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.ui.VerticalAlignment;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

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

    private final Color[] CHART_COLORS = {
            new Color(41, 128, 185),   // Blue
            new Color(142, 68, 173),   // Purple
            new Color(39, 174, 96),    // Green
            new Color(243, 156, 18),   // Orange
            new Color(231, 76, 60),    // Red
            new Color(52, 152, 219),   // Light Blue
            new Color(155, 89, 182),   // Light Purple
            new Color(46, 204, 113),   // Light Green
            new Color(241, 196, 15),   // Yellow
            new Color(230, 126, 34),   // Dark Orange
            new Color(192, 57, 43)     // Dark Red
    };

    private final Color HOUSING_COLOR = new Color(39, 174, 96);   // Green
    private final Color FOOD_COLOR = new Color(142, 68, 173);     // Purple
    private final Color SHOPPING_COLOR = new Color(231, 76, 60);  // Red
    private final Color TRANSPORT_COLOR = new Color(241, 196, 15); // Yellow
    private final Color UTILITIES_COLOR = new Color(52, 152, 219); // Light blue
    private final Color ENTERTAINMENT_COLOR = new Color(231, 76, 60); // Red-orange
    private final Color OTHER_COLOR = new Color(149, 165, 166);   // Gray

    public AnalyticsUI(int userId) {
        this.userId = userId;
        try {
            controller = new AnalyticsController(userId);
            
            // Apply dark theme to all charts
            applyDarkThemeToCharts();
            
            initializeUI();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error initializing analytics: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
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
        JPanel analyticsChartPanel = new JPanel(new GridLayout(2, 2, 20, 20)); // 2 rows, 2 columns, with gaps
        analyticsChartPanel.setBackground(BACKGROUND_COLOR);
        analyticsChartPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create charts as specified by the user
        JFreeChart savingsLineChart = createSavingsLineChart();     // Top left - Line chart for savings
        JFreeChart expenseDonutChart = createExpenseDonutChart();   // Top right - Hollow pie chart 
        JFreeChart incomeVsExpenseChart = createPieChart();         // Bottom left - Regular pie chart
        JFreeChart monthlyBarChart = createMonthlyBarChart();       // Bottom right - Monthly bar chart

        // Create chart panels with consistent size
        ChartPanel savingsPanel = new ChartPanel(savingsLineChart);
        ChartPanel expensesPanel = new ChartPanel(expenseDonutChart);
        ChartPanel incomePanel = new ChartPanel(incomeVsExpenseChart);
        ChartPanel comparisonPanel = new ChartPanel(monthlyBarChart);

        // Set preferred size for all panels
        Dimension panelSize = new Dimension(450, 280);
        savingsPanel.setPreferredSize(panelSize);
        expensesPanel.setPreferredSize(panelSize);
        incomePanel.setPreferredSize(panelSize);
        comparisonPanel.setPreferredSize(panelSize);
        
        // Set backgrounds
        savingsPanel.setBackground(BACKGROUND_COLOR);
        expensesPanel.setBackground(BACKGROUND_COLOR);
        incomePanel.setBackground(BACKGROUND_COLOR);
        comparisonPanel.setBackground(BACKGROUND_COLOR);

        analyticsChartPanel.add(savingsPanel);
        analyticsChartPanel.add(expensesPanel);
        analyticsChartPanel.add(incomePanel);
        analyticsChartPanel.add(comparisonPanel);

        // Add the chart panel to the main panel
        add(analyticsChartPanel, BorderLayout.CENTER);
    }

    private ChartPanel createStyledChartPanel(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setBackground(BACKGROUND_COLOR);
        return panel;
    }
    
    private void applyDarkTheme(JFreeChart chart) {
        // Set background color
        chart.setBackgroundPaint(BACKGROUND_COLOR);
        
        // Set title colors
        chart.getTitle().setPaint(TEXT_COLOR);
        
        // Handle plot depending on type
        if (chart.getPlot() instanceof PiePlot) {
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(BACKGROUND_COLOR);
            plot.setOutlinePaint(null);
            plot.setLabelBackgroundPaint(null);
            plot.setLabelOutlinePaint(null);
            plot.setLabelShadowPaint(null);
            plot.setLabelPaint(TEXT_COLOR);
            plot.setShadowPaint(null);
        } 
        else if (chart.getPlot() instanceof CategoryPlot) {
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(BACKGROUND_COLOR);
            plot.setOutlinePaint(null);
            plot.setRangeGridlinePaint(new Color(60, 40, 100));
            plot.setDomainGridlinePaint(new Color(60, 40, 100));
            
            // Styling axes
            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setAxisLinePaint(FIELD_BORDER);
            domainAxis.setTickLabelPaint(TEXT_COLOR);
            domainAxis.setTickMarkPaint(FIELD_BORDER);
            
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setAxisLinePaint(FIELD_BORDER);
            rangeAxis.setTickLabelPaint(TEXT_COLOR);
            rangeAxis.setTickMarkPaint(FIELD_BORDER);
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        }
        else if (chart.getPlot() instanceof XYPlot) {
            XYPlot plot = (XYPlot) chart.getPlot();
            plot.setBackgroundPaint(BACKGROUND_COLOR);
            plot.setOutlinePaint(null);
            plot.setRangeGridlinePaint(new Color(60, 40, 100));
            plot.setDomainGridlinePaint(new Color(60, 40, 100));
            
            // Styling axes
            DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
            domainAxis.setAxisLinePaint(FIELD_BORDER);
            domainAxis.setTickLabelPaint(TEXT_COLOR);
            domainAxis.setTickMarkPaint(FIELD_BORDER);
            
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setAxisLinePaint(FIELD_BORDER);
            rangeAxis.setTickLabelPaint(TEXT_COLOR);
            rangeAxis.setTickMarkPaint(FIELD_BORDER);
        }
        
        // Set legend styling if exists
        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(BACKGROUND_COLOR);
            chart.getLegend().setItemPaint(TEXT_COLOR);
            chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 10));
        }
    }

    // Apply dark theme to all JFreeChart charts
    private void applyDarkThemeToCharts() {
        // Create a custom theme
        StandardChartTheme darkTheme = new StandardChartTheme("Dark Theme");
        
        // Configure theme with colors
        darkTheme.setTitlePaint(TEXT_COLOR);
        darkTheme.setSubtitlePaint(new Color(200, 200, 200));
        darkTheme.setLegendBackgroundPaint(BACKGROUND_COLOR);
        darkTheme.setLegendItemPaint(TEXT_COLOR);
        darkTheme.setChartBackgroundPaint(BACKGROUND_COLOR);
        darkTheme.setPlotBackgroundPaint(BACKGROUND_COLOR);
        darkTheme.setPlotOutlinePaint(BACKGROUND_COLOR);
        darkTheme.setBaselinePaint(FIELD_BORDER);
        darkTheme.setCrosshairPaint(ACCENT_COLOR);
        darkTheme.setAxisLabelPaint(TEXT_COLOR);
        darkTheme.setTickLabelPaint(TEXT_COLOR);
        darkTheme.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        
        // Use a dark color for shadow - don't use null (causes errors)
        darkTheme.setShadowPaint(new Color(10, 10, 10));
        
        // Apply theme to all charts
        ChartFactory.setChartTheme(darkTheme);
    }

    // Create a line chart showing savings over time (daily or monthly)
    private JFreeChart createSavingsLineChart() {
        // Create time series dataset
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries series = new TimeSeries("Total Savings");
        
        // Get savings data
        Map<LocalDate, Double> savingsData = controller.getSavingsOverTime();
        
        // Check if we have enough data for monthly view (more than 30 data points)
        boolean showMonthly = savingsData.size() > 30;
        
        if (showMonthly) {
            // Aggregate by month for many data points
            Map<YearMonth, Double> monthlySavings = aggregateByMonth(savingsData);
            
            // Add monthly data to series with running total
            List<YearMonth> sortedMonths = new ArrayList<>(monthlySavings.keySet());
            Collections.sort(sortedMonths);
            
            double runningTotal = 0;
            for (YearMonth month : sortedMonths) {
                LocalDate middleOfMonth = month.atDay(15);
                runningTotal += monthlySavings.get(month);
                
                Day day = new Day(
                    middleOfMonth.getDayOfMonth(),
                    middleOfMonth.getMonthValue(),
                    middleOfMonth.getYear()
                );
                series.add(day, runningTotal);
            }
        } else {
            // Show daily data for fewer points
            List<LocalDate> sortedDates = new ArrayList<>(savingsData.keySet());
            Collections.sort(sortedDates);
            
            // Add daily data with cumulative total
            double runningTotal = 0;
            for (LocalDate date : sortedDates) {
                runningTotal += savingsData.get(date);
                
                Day day = new Day(
                    date.getDayOfMonth(),
                    date.getMonthValue(),
                    date.getYear()
                );
                series.add(day, runningTotal);
            }
        }
        
        // Add series to dataset
        dataset.addSeries(series);
        
        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Savings Trajectory",  // title
            "",                   // x-axis label
            "$",                  // y-axis label
            dataset,              // data
            true,                 // show legend
            true,                 // show tooltips
            false                 // no urls
        );
        
        // Apply dark theme
        applyDarkTheme(chart);
        
        // Add subtitle
        String subtitleText = showMonthly ? "Your monthly savings accumulation" : "Your daily savings accumulation";
        TextTitle subtitle = new TextTitle(
            subtitleText,
            new Font("Arial", Font.PLAIN, 12),
            TEXT_COLOR,
            RectangleEdge.TOP,
            HorizontalAlignment.CENTER,
            VerticalAlignment.TOP,
            TextTitle.DEFAULT_PADDING
        );
        chart.addSubtitle(subtitle);
        
        // Customize appearance
        XYPlot plot = chart.getXYPlot();
        
        // Set line renderer with thicker line
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, CORRECT_COLOR); // Green for savings
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6));
        plot.setRenderer(renderer);
        
        // Format axes
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 11));
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new DecimalFormat("$#,##0"));
        
        return chart;
    }

    // Create a donut chart for expenses by category
    private JFreeChart createExpenseDonutChart() {
        // Create dataset
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        
        // Get expenses by category
        Map<String, Double> expensesByCategory = controller.getExpensesByCategory();
        
        // Calculate total
        double totalExpenses = expensesByCategory.values().stream()
            .mapToDouble(Double::doubleValue).sum();
        
        // Add data with percentages
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            if (totalExpenses > 0) {
                double percentage = (entry.getValue() / totalExpenses) * 100;
                String key = entry.getKey() + " (" + String.format("%.0f%%", percentage) + ")";
                dataset.setValue(key, entry.getValue());
            } else {
                dataset.setValue(entry.getKey() + " (0%)", entry.getValue());
            }
        }
        
        // Handle empty dataset
        if (dataset.getKeys().isEmpty()) {
            dataset.setValue("No Expenses", 1.0);
        }
        
        // Create a ring chart (donut chart)
        JFreeChart chart = ChartFactory.createRingChart(
            "Expense Distribution",  // title
            dataset,                // data
            true,                   // include legend
            true,                   // tooltips
            false                   // urls
        );
        
        // Apply dark theme
        applyDarkTheme(chart);
        
        // Add subtitle
        TextTitle subtitle = new TextTitle(
            "Your spending across different realms",
            new Font("Arial", Font.PLAIN, 12),
            TEXT_COLOR,
            RectangleEdge.TOP,
            HorizontalAlignment.CENTER,
            VerticalAlignment.TOP,
            TextTitle.DEFAULT_PADDING
        );
        chart.addSubtitle(subtitle);
        
        // Customize appearance
        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.setLabelBackgroundPaint(null);
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}"));
        plot.setSectionDepth(0.35); // Make donut thinner
        plot.setShadowPaint(null);
        
        // Assign colors to categories
        for (Comparable key : dataset.getKeys()) {
            String categoryName = key.toString().split(" \\(")[0]; // Extract category name without percentage
            
            if (categoryName.equalsIgnoreCase("Housing")) {
                plot.setSectionPaint(key, HOUSING_COLOR);
            } else if (categoryName.equalsIgnoreCase("Food")) {
                plot.setSectionPaint(key, FOOD_COLOR);
            } else if (categoryName.equalsIgnoreCase("Shopping")) {
                plot.setSectionPaint(key, SHOPPING_COLOR);
            } else if (categoryName.equalsIgnoreCase("Transport") || 
                      categoryName.equalsIgnoreCase("Transportation")) {
                plot.setSectionPaint(key, TRANSPORT_COLOR);
            } else if (categoryName.equalsIgnoreCase("Utilities")) {
                plot.setSectionPaint(key, UTILITIES_COLOR);
            } else if (categoryName.equalsIgnoreCase("Entertainment")) {
                plot.setSectionPaint(key, ENTERTAINMENT_COLOR);
            } else if (categoryName.equalsIgnoreCase("No Expenses")) {
                plot.setSectionPaint(key, FIELD_BORDER);
            } else {
                // Use colors from our chart color array
                int index = Math.abs(categoryName.hashCode()) % CHART_COLORS.length;
                plot.setSectionPaint(key, CHART_COLORS[index]);
            }
        }
        
        return chart;
    }

    // Create a pie chart for income vs expenses
    private JFreeChart createPieChart() {
        // Create dataset
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        
        // Get total income and expense
        double totalIncome = controller.getTotalIncome();
        double totalExpense = controller.getTotalExpense();
        
        // Format with percentages
        double total = totalIncome + totalExpense;
        if (total > 0) {
            double incomePercentage = (totalIncome / total) * 100;
            double expensePercentage = (totalExpense / total) * 100;
            
            dataset.setValue("Income (" + String.format("%.0f%%", incomePercentage) + ")", totalIncome);
            dataset.setValue("Expense (" + String.format("%.0f%%", expensePercentage) + ")", totalExpense);
        } else {
            dataset.setValue("Income (0%)", 1);
            dataset.setValue("Expense (0%)", 1);
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
            "Income vs Expenses",  // title
            dataset,              // data
            true,                 // include legend
            true,                 // tooltips
            false                 // urls
        );
        
        // Apply dark theme
        applyDarkTheme(chart);
        
        // Add subtitle
        TextTitle subtitle = new TextTitle(
            "Total distribution",
            new Font("Arial", Font.PLAIN, 12),
            TEXT_COLOR,
            RectangleEdge.TOP,
            HorizontalAlignment.CENTER,
            VerticalAlignment.TOP,
            TextTitle.DEFAULT_PADDING
        );
        chart.addSubtitle(subtitle);
        
        // Customize appearance
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.setLabelBackgroundPaint(null);
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: ${1}"));
        
        // Set colors for income and expense
        plot.setSectionPaint("Income (" + String.format("%.0f%%", (totalIncome / total) * 100) + ")", CORRECT_COLOR);
        plot.setSectionPaint("Expense (" + String.format("%.0f%%", (totalExpense / total) * 100) + ")", INCORRECT_COLOR);
        
        return chart;
    }

    // Create a monthly bar chart for income vs expenses
    private JFreeChart createMonthlyBarChart() {
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get income and expense data
        Map<LocalDate, Double> incomeData = controller.getIncomeData();
        Map<LocalDate, Double> expenseData = controller.getExpenseData();
        
        // Aggregate by month
        Map<YearMonth, Double> monthlyIncome = aggregateByMonth(incomeData);
        Map<YearMonth, Double> monthlyExpenses = aggregateByMonth(expenseData);
        
        // Combine all months
        Set<YearMonth> allMonths = new TreeSet<>(); // TreeSet sorts automatically
        allMonths.addAll(monthlyIncome.keySet());
        allMonths.addAll(monthlyExpenses.keySet());
        
        // Add data to dataset
        for (YearMonth month : allMonths) {
            String monthLabel = month.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + 
                                " " + month.getYear();
            
            double income = monthlyIncome.getOrDefault(month, 0.0);
            double expense = monthlyExpenses.getOrDefault(month, 0.0);
            
            dataset.addValue(income, "Income", monthLabel);
            dataset.addValue(expense, "Expense", monthLabel);
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Income vs Expenses Over Time", // title
            "",                           // x-axis label
            "Amount",                     // y-axis label
            dataset,                      // data
            PlotOrientation.VERTICAL,     // orientation
            true,                         // include legend
            true,                         // tooltips
            false                         // urls
        );
        
        // Apply dark theme
        applyDarkTheme(chart);
        
        // Add subtitle
        TextTitle subtitle = new TextTitle(
            "Monthly performance",
            new Font("Arial", Font.PLAIN, 12),
            TEXT_COLOR,
            RectangleEdge.TOP,
            HorizontalAlignment.CENTER,
            VerticalAlignment.TOP,
            TextTitle.DEFAULT_PADDING
        );
        chart.addSubtitle(subtitle);
        
        // Customize appearance
        CategoryPlot plot = chart.getCategoryPlot();
        
        // Set custom bar colors
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, CORRECT_COLOR); // Green for income
        renderer.setSeriesPaint(1, INCORRECT_COLOR); // Red for expense
        renderer.setBarPainter(new StandardBarPainter()); // Remove glossy effect
        renderer.setShadowVisible(false);
        
        // Add value labels on bars
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("${2}", new DecimalFormat("#,##0")));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Arial", Font.BOLD, 10));
        renderer.setDefaultItemLabelPaint(TEXT_COLOR);
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
            ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
        
        // Format axes
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.4); // More space between month groups
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 11));
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new DecimalFormat("$#,##0"));
        
        return chart;
    }

    // Helper method to aggregate data by month
    private Map<YearMonth, Double> aggregateByMonth(Map<LocalDate, Double> data) {
        Map<YearMonth, Double> result = new HashMap<>();
        
        for (Map.Entry<LocalDate, Double> entry : data.entrySet()) {
            YearMonth yearMonth = YearMonth.from(entry.getKey());
            result.put(yearMonth, result.getOrDefault(yearMonth, 0.0) + entry.getValue());
        }
        
        return result;
    }
} 