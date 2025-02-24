package com.fbg.test;

import model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    public void testAddPoints() {
        User user = new User("Alice", "alice@test.com", 1000);
        user.addPoints(50);
        assertEquals(50, user.getPoints());
    }

    @Test
    public void testUpdateBalance() {
        User user = new User("Alice", "alice@test.com", 1000);
        user.updateBalance(-200);
        assertEquals(800, user.getBalance());
    }
} 