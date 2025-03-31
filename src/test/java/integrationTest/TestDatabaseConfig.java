package integrationTest;

import database.DatabaseConfig;
import database.StubDatabaseConfig;

public class TestDatabaseConfig {
    private static StubDatabaseConfig instance;

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new StubDatabaseConfig(true);
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }
} 