package database.dao;

import database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Budgetimpl implements Budget{
    @Override
    public double getTotalIncomeForMonth(int userId) throws SQLException {
        // First day of current month
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
        // Last day of current month
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

        String sql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND type = 'income' " +
                "AND date BETWEEN ? AND ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setDate(2, java.sql.Date.valueOf(firstDay));
            pstmt.setDate(3, java.sql.Date.valueOf(lastDay));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double sum = rs.getDouble(1);
                    return rs.wasNull() ? 0 : sum;
                }
            }
        }

        return 0;
    }

    @Override
    public double getTotalExpensesForMonth(int userId) throws SQLException {
        // First day of current month
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
        // Last day of current month
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

        String sql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND type = 'expense' " +
                "AND date BETWEEN ? AND ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setDate(2, java.sql.Date.valueOf(firstDay));
            pstmt.setDate(3, java.sql.Date.valueOf(lastDay));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double sum = rs.getDouble(1);
                    return rs.wasNull() ? 0 : sum;
                }
            }
        }

        return 0;
    }
}
