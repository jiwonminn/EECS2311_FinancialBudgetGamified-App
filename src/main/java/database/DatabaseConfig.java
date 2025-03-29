package database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConfig {
    Connection getConnection() throws SQLException;
    void initializeDatabase() throws SQLException;
    void closeConnection() throws SQLException;
} 