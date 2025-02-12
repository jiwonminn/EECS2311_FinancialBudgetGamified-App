package controller;

import controller.BudgetController;

// This file will be removed in the final version of the project

public class Main {
    public static void main(String[] args) {
        try {
            BudgetController budgetController = new BudgetController(1000, 200);
            budgetController.checkBudgetAndNotify("write the user email", "user name", 250); // Replace with real data
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}