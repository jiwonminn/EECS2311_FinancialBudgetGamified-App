package database.dao;

import java.sql.SQLException;

public interface Budget {
    double getTotalIncomeForMonth(int userId) throws SQLException;
    double getTotalExpensesForMonth(int userId) throws SQLException;
}
