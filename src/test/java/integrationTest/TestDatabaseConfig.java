package integrationTest;

import database.DatabaseConfig;
import database.StubDatabaseConfig;

public class TestDatabaseConfig {
    private static DatabaseConfig instance;

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new StubDatabaseConfig();
        }
        return instance;
    }
} 