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
import java.util.regex.Pattern;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.util.Arrays;

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

        try (CSVReader csvReader = new CSVReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String[] record;

            // Read and discard the header line
            csvReader.readNext(); // This reads the header line and discards it

            while ((record = csvReader.readNext()) != null) {
                // Check for the expected number of columns
                if (record.length < 4) { // At least Date, Description, Amount (Debit/Credit)
                    throw new IllegalArgumentException("Invalid CSV format: insufficient columns in record " + Arrays.toString(record));
                }

                // Extract fields based on common column names
                String date = record[0]; // Assuming the first column is Date
                String description = record[1]; // Assuming the second column is Description
                String debit = record[2]; // Assuming the third column is Debit
                String credit = record[3]; // Assuming the fourth column is Credit

                // Validate the format of the transaction
                if (!isValidTransaction(date, description, debit, credit)) {
                    throw new IllegalArgumentException("Invalid transaction format: " + Arrays.toString(record));
                }

                // Skip if both debit and credit are empty
                if (debit.isEmpty() && credit.isEmpty()) {
                    continue;
                }

                // Convert date from MM/dd/yyyy to yyyy-MM-dd
                date = convertDateFormat(date);

                String[] transaction = new String[4];
                transaction[0] = date;  // Date in yyyy-MM-dd format
                transaction[1] = !debit.isEmpty() ? debit : credit;  // Amount
                transaction[2] = determineCategory(description);  // Category based on description
                transaction[3] = description;  // Description

                transactions.add(transaction);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            throw e; // Rethrow the exception after logging
        }
        return transactions;
    }

    /**
     * Retrieves the value of a field from the CSV record based on possible headers.
     */
    private String getFieldValue(CSVRecord record, String... possibleHeaders) {
        for (String header : possibleHeaders) {
            int index = record.getParser().getHeaderMap().get(header);
            if (index >= 0 && index < record.size()) {
                return record.get(index).trim();
            }
        }
        return ""; // Return empty if no valid header found
    }

    /**
     * Converts date from various formats to yyyy-MM-dd.
     */
    private String convertDateFormat(String date) {
        // Example: Handle MM/dd/yyyy and dd-MM-yyyy formats
        String[] dateParts;
        if (date.contains("/")) {
            dateParts = date.split("/");
            if (dateParts.length == 3) {
                return dateParts[2] + "-" + String.format("%02d", Integer.parseInt(dateParts[0])) + "-" +
                        String.format("%02d", Integer.parseInt(dateParts[1]));
            }
        } else if (date.contains("-")) {
            dateParts = date.split("-");
            if (dateParts.length == 3) {
                return dateParts[2] + "-" + String.format("%02d", Integer.parseInt(dateParts[1])) + "-" +
                        String.format("%02d", Integer.parseInt(dateParts[0]));
            }
        }
        // If no valid format is found, return the original date (or handle as needed)
        return date;
    }

    /**
     * Validates the format of a transaction.
     * @return true if the transaction format is valid, false otherwise.
     */
    private boolean isValidTransaction(String date, String description, String debit, String credit) {
        // Check if the date is in the correct format (MM/dd/yyyy or dd-MM-yyyy)
        if (!Pattern.matches("\\d{1,2}/\\d{1,2}/\\d{4}", date) && !Pattern.matches("\\d{1,2}-\\d{1,2}-\\d{4}", date)) {
            return false;
        }

        // Check if the description is not empty
        if (description == null || description.trim().isEmpty()) {
            return false;
        }

        // Check if either debit or credit has a valid number
        if (!debit.isEmpty() && !isNumeric(debit)) {
            return false;
        }
        if (!credit.isEmpty() && !isNumeric(credit)) {
            return false;
        }

        return true;
    }

    /**
     * Checks if a string is a valid number.
     */
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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