package controller;
import java.util.ArrayList;
import java.util.List;
import model.Transaction;
public class TransactionController {
	private List<Transaction> transactions;

    public TransactionController() {
        transactions = new ArrayList<>();
    }

    public void addTransaction(String date, String description, double amount) {
        transactions.add(new Transaction(date, description, amount));
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
}
