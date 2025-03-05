package view;

import controller.UserController;
import javax.swing.*;

public class MainUI {
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
