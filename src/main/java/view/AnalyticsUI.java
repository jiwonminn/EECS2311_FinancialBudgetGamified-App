package view;

import controller.AnalyticsController;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
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
}