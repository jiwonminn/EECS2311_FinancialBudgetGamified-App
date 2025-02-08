package controller;

import controller.BudgetController;



public class Main {
    public static void main(String[] args) {
        try {
            BudgetController budgetController = new BudgetController(1000, 200);
            budgetController.checkBudgetAndNotify("receivers email ", "John Doe", 250); // Replace with real data
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}