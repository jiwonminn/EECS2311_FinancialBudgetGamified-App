package controller;

import controller.BudgetController;

/**
 * @deprecated This class has been replaced by app.Main.
 * Please use app.Main as the main entry point for the application.
 */
@Deprecated
public class Main {
    /**
     * @deprecated This method has been replaced by app.Main.main().
     * Please use app.Main.main() as the main entry point for the application.
     */
    @Deprecated
    public static void main(String[] args) {
        System.out.println("This main method is deprecated. Please use app.Main.main() instead.");
        // Forward to the new main method
        app.Main.main(args);
    }
}