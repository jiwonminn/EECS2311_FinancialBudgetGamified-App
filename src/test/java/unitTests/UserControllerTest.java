package unitTests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import controller.UserController;
import org.junit.jupiter.api.Test;

public class UserControllerTest {

    @Test
    public void testAddPoints() throws SQLException {
        // Use constructor with explicit userId to avoid triggering SessionManager logic.
        UserController controller = new UserController(2, "testUser", "test@example.com", 100.0);
        // Assuming initial points is 0.
        assertEquals(0, controller.getPoints(), "Initial points should be 0");
        controller.addPoints(10);
        assertEquals(10, controller.getPoints(), "Points should be updated to 10 after adding");
    }

    @Test
    public void testUpdateBalance() throws SQLException {
        UserController controller = new UserController(3, "testUser", "test@example.com", 100.0);
        // Assume updateBalance adds the given amount to the current balance.
        controller.updateBalance(50.0);
        // The getUserInfo returns a string that includes the balance.
        String userInfo = controller.getUserInfo();
        assertTrue(userInfo.contains("Balance: 150.0"), "User balance should be updated to 150.0");
    }

    @Test
    public void testSetAndGetUserId() throws SQLException {
        // Use the constructor that may trigger SessionManager; then explicitly set the user ID.
        UserController controller = new UserController("testUser", "test@example.com", 100.0);
        controller.setUserId(5);
        assertEquals(5, controller.getUserId(), "User ID should be set to 5");
    }

    @Test
    public void testGetUserInfo() throws SQLException {
        UserController controller = new UserController(10, "john_doe", "john@example.com", 200.0);
        controller.addPoints(20);
        String info = controller.getUserInfo();
        assertTrue(info.contains("john_doe"), "User info should contain username");
        assertTrue(info.contains("john@example.com"), "User info should contain email");
        assertTrue(info.contains("200.0"), "User info should contain balance");
        assertTrue(info.contains("20"), "User info should contain points");
    }
}
