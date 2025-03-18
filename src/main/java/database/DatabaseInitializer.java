package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initializeDatabase() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            System.out.println("Failed to get DB connection!");
            return;
        }
        try {
            createTables(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id SERIAL PRIMARY KEY,"
                + "email VARCHAR(100) UNIQUE NOT NULL,"
                + "password VARCHAR(100) NOT NULL"
                + ")";

        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
                + "id SERIAL PRIMARY KEY,"
                + "user_id INT NOT NULL,"
                + "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "description TEXT NOT NULL,"
                + "category VARCHAR(50) NOT NULL,"
                + "type VARCHAR(50) NOT NULL,"
                + "amount DOUBLE PRECISION NOT NULL,"    // Changed from DOUBLE to DOUBLE PRECISION
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ")";

        String createUserBudgetTable = "CREATE TABLE IF NOT EXISTS user_budget ("
                + "id SERIAL PRIMARY KEY,"
                + "user_id INT NOT NULL,"
                + "total_budget DOUBLE PRECISION NOT NULL DEFAULT 0.0,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ")";
                
        String createQuestsTable = "CREATE TABLE IF NOT EXISTS quests ("
                + "id SERIAL PRIMARY KEY, "
                + "user_id INTEGER NOT NULL, "
                + "title VARCHAR(255) NOT NULL, "
                + "description TEXT, "
                + "quest_type VARCHAR(50) NOT NULL, "  // DAILY, WEEKLY, MONTHLY
                + "xp_reward INTEGER NOT NULL, "
                + "required_amount DOUBLE PRECISION, "
                + "completion_status BOOLEAN NOT NULL DEFAULT FALSE, "
                + "deadline DATE, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ")";
        
        String createUserExperienceTable = "CREATE TABLE IF NOT EXISTS user_experience ("
                + "user_id INTEGER PRIMARY KEY, "
                + "current_xp INTEGER NOT NULL DEFAULT 0, "
                + "level INTEGER NOT NULL DEFAULT 1, "
                + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ")";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createTransactionsTable);
            stmt.execute(createUserBudgetTable);
            stmt.execute(createQuestsTable);
            stmt.execute(createUserExperienceTable);

            // Create a default budget entry for existing users who don't have one
            String insertDefaultBudgets = "INSERT INTO user_budget (user_id, total_budget) "
                    + "SELECT id, 0.0 FROM users u "
                    + "WHERE NOT EXISTS (SELECT 1 FROM user_budget ub WHERE ub.user_id = u.id)";
            stmt.execute(insertDefaultBudgets);
            
            // Create default user experience entries for existing users who don't have one
            String insertDefaultExperience = "INSERT INTO user_experience (user_id, current_xp, level) "
                    + "SELECT id, 0, 1 FROM users u "
                    + "WHERE NOT EXISTS (SELECT 1 FROM user_experience ue WHERE ue.user_id = u.id)";
            stmt.execute(insertDefaultExperience);
        }
    }
}
