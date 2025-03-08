package model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.nio.charset.StandardCharsets;

public class Analytics {
    private Map<String, Double> categorySpending;
    private Map<LocalDate, Double> dailySpending;
    private double totalBudget;
    private double totalSpent;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Analytics() {
        this.categorySpending = new HashMap<>();
        this.dailySpending = new HashMap<>();
        this.totalBudget = 0.0;
        this.totalSpent = 0.0;
    }

    public List<String[]> readCSVFile(File file) throws IOException {
        List<String[]> transactions = new ArrayList<>();
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("Date", "Description", "Debit", "Credit", "Balance")
                .setSkipHeaderRecord(true)
                .build();

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {

            for (CSVRecord record : parser) {
                if (record.size() >= 5) {  // TD Bank format has 5 columns
                    String date = record.get("Date");
                    String description = record.get("Description");
                    String debit = record.get("Debit").trim();    // Expense
                    String credit = record.get("Credit").trim();  // Income

                    // Skip if both debit and credit are empty
                    if (debit.isEmpty() && credit.isEmpty()) {
                        continue;
                    }

                    // Convert date from MM/dd/yyyy to yyyy-MM-dd
                    String[] dateParts = date.split("/");
                    if (dateParts.length == 3) {
                        date = dateParts[2] + "-" + String.format("%02d", Integer.parseInt(dateParts[0])) + "-" +
                                String.format("%02d", Integer.parseInt(dateParts[1]));
                    }

                    String[] transaction = new String[4];
                    transaction[0] = date;  // Date in yyyy-MM-dd format
                    transaction[1] = !debit.isEmpty() ? debit : credit;  // Amount
                    transaction[2] = determineCategory(description);  // Category based on description
                    transaction[3] = description;  // Description

                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }

    // Helper method to determine category based on description
    private String determineCategory(String description) {
        description = description.toLowerCase();

        if (description.contains("grocery") || description.contains("food") || description.contains("restaurant") ||
                description.contains("uber eat") || description.contains("doordash")) {
            return "Food & Dining";
        } else if (description.contains("amazon") || description.contains("walmart") || description.contains("shopping")) {
            return "Shopping";
        } else if (description.contains("netflix") || description.contains("spotify") || description.contains("movie") ||
                description.contains("entertainment")) {
            return "Entertainment";
        } else if (description.contains("gas") || description.contains("uber") || description.contains("lyft") ||
                description.contains("transit")) {
            return "Transport";
        } else if (description.contains("rent") || description.contains("mortgage")) {
            return "Housing";
        } else if (description.contains("hydro") || description.contains("water") || description.contains("internet") ||
                description.contains("phone")) {
            return "Utilities";
        } else if (description.contains("salary") || description.contains("deposit") || description.contains("payment")) {
            return "Income";
        } else if (description.contains("health") || description.contains("medical") || description.contains("pharmacy")) {
            return "Healthcare";
        } else if (description.contains("school") || description.contains("tuition") || description.contains("book")) {
            return "Education";
        }

        return "Other";
    }

    public void processTransaction(String[] transaction) {
        try {
            LocalDate date = LocalDate.parse(transaction[0].trim(), DATE_FORMATTER);
            double amount = Double.parseDouble(transaction[1].trim());
            String category = transaction[2].trim();

            updateCategorySpending(category, amount);
            updateDailySpending(date, amount);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid transaction format: " + String.join(",", transaction));
        }
    }

    public void updateCategorySpending(String category, double amount) {
        categorySpending.merge(category, amount, Double::sum);
    }

    public void updateDailySpending(LocalDate date, double amount) {
        dailySpending.merge(date, amount, Double::sum);
    }

    public void setTotalBudget(double budget) {
        this.totalBudget = budget;
    }

    public Map<String, Double> getCategorySpending() {
        return new HashMap<>(categorySpending);
    }

    public Map<LocalDate, Double> getDailySpending() {
        return new HashMap<>(dailySpending);
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public double getRemainingBudget() {
        return totalBudget - totalSpent;
    }

    public double getSpendingPercentage() {
        return totalBudget > 0 ? (totalSpent / totalBudget) * 100 : 0;
    }

    public void setTotalSpent(double amount) {
        this.totalSpent = amount;
    }
}