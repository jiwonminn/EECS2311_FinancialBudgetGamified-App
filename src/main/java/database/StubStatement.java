package database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StubStatement implements Statement {
    protected final Map<Integer, Map<String, Object>> users;
    protected ResultSet resultSet;
    protected boolean closed = false;

    public StubStatement(Map<Integer, Map<String, Object>> users) {
        this.users = users;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        if (sql.toLowerCase().contains("select")) {
            // Handle SELECT queries
            if (sql.toLowerCase().contains("users")) {
                resultSet = new StubResultSet(users);
                return resultSet;
            }
        }
        throw new SQLException("Unsupported query: " + sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        if (sql.toLowerCase().contains("insert")) {
            // Handle INSERT queries
            if (sql.toLowerCase().contains("users")) {
                // Extract values from SQL (simplified)
                String values = sql.substring(sql.indexOf("VALUES") + 6).trim();
                values = values.substring(1, values.length() - 1);
                String[] parts = values.split(",");
                String email = parts[0].trim().substring(1, parts[0].trim().length() - 1);
                String password = parts[1].trim().substring(1, parts[1].trim().length() - 1);
                
                // Add new user
                int newId = users.size() + 1;
                Map<String, Object> userData = new HashMap<>();
                userData.put("email", email);
                userData.put("password", password);
                userData.put("balance", 0.0);
                userData.put("points", 0);
                users.put(newId, userData);
                return 1;
            }
        } else if (sql.toLowerCase().contains("delete")) {
            // Handle DELETE queries
            if (sql.toLowerCase().contains("users")) {
                // Extract user ID from SQL (simplified)
                String idStr = sql.substring(sql.indexOf("id =") + 4).trim();
                int id = Integer.parseInt(idStr);
                if (users.remove(id) != null) {
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public void close() throws SQLException {
        closed = true;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        // No-op for stub
    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        // No-op for stub
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        // No-op for stub
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        // No-op for stub
    }

    @Override
    public void cancel() throws SQLException {
        // No-op for stub
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        // No-op for stub
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        // No-op for stub
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        if (sql.toLowerCase().contains("select")) {
            resultSet = executeQuery(sql);
            return true;
        } else {
            executeUpdate(sql);
            return false;
        }
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return resultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return -1;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        // No-op for stub
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        // No-op for stub
    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        // No-op for stub
    }

    @Override
    public void clearBatch() throws SQLException {
        // No-op for stub
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new StubConnection(users);
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return new StubResultSet(users);
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return execute(sql);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return execute(sql);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return execute(sql);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        // No-op for stub
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        // No-op for stub
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }
        throw new SQLException("Cannot unwrap to " + iface.getName());
    }
} 