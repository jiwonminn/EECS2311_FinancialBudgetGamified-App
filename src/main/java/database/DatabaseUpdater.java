//package database;
//
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
///**
// * Utility class to check and update database schema if needed
// */
//public class DatabaseUpdater {
//
//    /**
//     * Checks if the 'category' column exists in the transactions table and adds it if it doesn't
//     * @return true if the update was successful or the column already exists
//     */
//    public static boolean updateTransactionsTable() {
//        Connection conn = DatabaseManager.getConnection();
//        boolean columnExists = false;
//
//        try {
//            // Check if the column exists
//            DatabaseMetaData meta = conn.getMetaData();
//            ResultSet rs = meta.getColumns(null, null, "transactions", "category");
//
//            if (rs.next()) {
//                // Column already exists
//                System.out.println("Category column already exists in transactions table.");
//                columnExists = true;
//            } else {
//                // Add the column
//                try (Statement stmt = conn.createStatement()) {
//                    String sql = "ALTER TABLE transactions ADD COLUMN category VARCHAR(100) DEFAULT 'Other'";
//                    stmt.executeUpdate(sql);
//                    System.out.println("Category column successfully added to transactions table!");
//                    columnExists = true;
//                } catch (SQLException e) {
//                    System.err.println("Failed to add category column: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//
//            rs.close();
//        } catch (SQLException e) {
//            System.err.println("Error checking for category column: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        return columnExists;
//    }
//}