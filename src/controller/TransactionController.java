package controller;
import java.util.ArrayList;
import java.util.List;
import model.Transaction;
import java.util.stream.Collectors;
import java.util.Comparator;

public class TransactionController {
	private List<Transaction> transactions;

    public TransactionController() {
        transactions = new ArrayList<>();
    }

    public void addTransaction(String date, String description, double amount) {
        transactions.add(new Transaction(date, description, amount));
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions); 
    }
    
    public void displayTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions available.");
        } else {
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
    }

 // Sort transactions
    public List<Transaction> sortTransactions(String criteria) {
        switch (criteria.toLowerCase()) {
            case "date":
                transactions.sort(Comparator.comparing(Transaction::getDate));
                break;
            case "amount":
                transactions.sort(Comparator.comparingDouble(Transaction::getAmount));
                break;
            case "description":
                transactions.sort(Comparator.comparing(Transaction::getDescription));
                break;
            default:
                System.out.println("Invalid sorting criteria! Use 'date', 'amount', or 'description'.");
                return null;
        }
        System.out.println("Transactions sorted by " + criteria + ":");
        displayTransactions();
    return transactions;
    }

    // Filter transactions by date range
    public List<Transaction> filterByDateRange(String startDate, String endDate) {
        List<Transaction> filtered = transactions.stream()
            .filter(t -> t.getDate().compareTo(startDate) >= 0 && t.getDate().compareTo(endDate) <= 0)
            .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("No transactions found in the given date range.");
        } else {
            System.out.println("Transactions between " + startDate + " and " + endDate + ":");
            filtered.forEach(System.out::println);
        }
        return filtered;
    }

    // Filter transactions by description keyword
    public List<Transaction> filterByDescription(String keyword) {
        List<Transaction> filtered = transactions.stream()
            .filter(t -> t.getDescription().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("No transactions found containing: " + keyword);
        } else {
            System.out.println("Transactions containing '" + keyword + "':");
            filtered.forEach(System.out::println);
        }
        return filtered;
    }




}
