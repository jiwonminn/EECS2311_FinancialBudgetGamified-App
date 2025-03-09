package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                + "amount DOUBLE NOT NULL,"
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ")";

        String createUserBudgetTable = "CREATE TABLE IF NOT EXISTS user_budget ("
                + "id SERIAL PRIMARY KEY,"                           // Use SERIAL instead of INT AUTO_INCREMENT
                + "user_id INT NOT NULL,"
                + "total_budget DOUBLE PRECISION NOT NULL DEFAULT 0.0,"  // Use DOUBLE PRECISION
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"   // Remove ON UPDATE clause; use a trigger if auto-update is required
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ")";


        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createTransactionsTable);
            stmt.execute(createUserBudgetTable);

            // Create a default budget entry for existing users who don't have one
            String insertDefaultBudgets = "INSERT INTO user_budget (user_id, total_budget) "
                    + "SELECT id, 0.0 FROM users u "
                    + "WHERE NOT EXISTS (SELECT 1 FROM user_budget ub WHERE ub.user_id = u.id)";
            stmt.execute(insertDefaultBudgets);
        }
    }
}
