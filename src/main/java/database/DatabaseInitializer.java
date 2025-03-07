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
                + "amount DOUBLE PRECISION NOT NULL,"
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ")";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createTransactionsTable);
        }
    }
}
