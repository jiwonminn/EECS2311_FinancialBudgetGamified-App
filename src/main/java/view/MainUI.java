package view;

import main.java.controller.UserController;

public class MainUI {
    public static void main(String[] args) {
        UserController userController = new UserController("Alice", "hhhhh",1000);
        
        System.out.println("Welcome to Financial Budget Gamified App!");
        System.out.println(userController.getUserInfo());
        
        userController.updateBalance(-200);
        userController.addPoints(10);

        System.out.println("Updated User Info: " + userController.getUserInfo());
    }
}
