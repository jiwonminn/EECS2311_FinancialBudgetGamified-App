package database;

import java.sql.*;
import java.util.Map;
import java.util.Calendar;
import java.util.HashMap;

public class StubPreparedStatement extends StubStatement implements PreparedStatement {
    private final String sql;
    private final Object[] parameters;
    private final Map<Integer, Map<String, Object>> userExperience;

    public StubPreparedStatement(Map<Integer, Map<String, Object>> users, Map<Integer, Map<String, Object>> userExperience, String sql) {
        super(users, userExperience);
        this.sql = sql;
        this.parameters = new Object[10]; // Support up to 10 parameters
        this.userExperience = userExperience;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        if (sql.toLowerCase().contains("select")) {
            if (sql.toLowerCase().contains("user_experience")) {
                if (sql.toLowerCase().contains("user_id = ?")) {
                    // Handle SELECT * FROM user_experience WHERE user_id = ?
                    Object param = parameters[0];
                    if (param == null) {
                        throw new SQLException("User ID cannot be null");
                    }
                    int userId = param instanceof Integer ? (Integer) param : Integer.parseInt(param.toString());
                    if (userExperience.containsKey(userId)) {
                        resultSet = new StubResultSet(userExperience, userId);
                        return resultSet;
                    }
                    // Return empty result set if user not found
                    resultSet = new StubResultSet(userExperience);
                    return resultSet;
                }
            } else if (sql.toLowerCase().contains("users")) {
                if (sql.toLowerCase().contains("id = ?")) {
                    // Handle SELECT * FROM users WHERE id = ?
                    Object param = parameters[0];
                    if (param == null) {
                        throw new SQLException("User ID cannot be null");
                    }
                    int userId = param instanceof Integer ? (Integer) param : Integer.parseInt(param.toString());
                    if (users.containsKey(userId)) {
                        resultSet = new StubResultSet(users, userId);
                        return resultSet;
                    }
                    // Return empty result set if user not found
                    resultSet = new StubResultSet(users);
                    return resultSet;
                } else if (sql.toLowerCase().contains("email = ?") && !sql.toLowerCase().contains("password")) {
                    // Handle SELECT * FROM users WHERE email = ?
                    String email = (String) parameters[0];
                    for (Map.Entry<Integer, Map<String, Object>> entry : users.entrySet()) {
                        if (email.equals(entry.getValue().get("email"))) {
                            resultSet = new StubResultSet(users, entry.getKey());
                            return resultSet;
                        }
                    }
                    // Return empty result set if user not found
                    resultSet = new StubResultSet(users);
                    return resultSet;
                } else if (sql.toLowerCase().contains("email = ? and password = ?")) {
                    // Handle SELECT id FROM users WHERE email = ? AND password = ?
                    String email = (String) parameters[0];
                    String password = (String) parameters[1];
                    for (Map.Entry<Integer, Map<String, Object>> entry : users.entrySet()) {
                        if (email.equals(entry.getValue().get("email")) && 
                            password.equals(entry.getValue().get("password"))) {
                            // Create a new result set with the user ID
                            resultSet = new StubResultSet(users, entry.getKey());
                            return resultSet;
                        }
                    }
                    // Return empty result set if user not found
                    resultSet = new StubResultSet(users);
                    return resultSet;
                }
            }
        }
        throw new SQLException("Unsupported query: " + sql);
    }

    @Override
    public int executeUpdate() throws SQLException {
        if (sql.toLowerCase().contains("insert")) {
            if (sql.toLowerCase().contains("user_experience")) {
                // Handle INSERT INTO user_experience (user_id, current_xp, level) VALUES (?, ?, ?)
                Object userIdParam = parameters[0];
                Object xpParam = parameters[1];
                Object levelParam = parameters[2];
                
                if (userIdParam == null || xpParam == null || levelParam == null) {
                    throw new SQLException("Parameters cannot be null");
                }
                
                int userId = userIdParam instanceof Integer ? (Integer) userIdParam : Integer.parseInt(userIdParam.toString());
                int xp = xpParam instanceof Integer ? (Integer) xpParam : Integer.parseInt(xpParam.toString());
                int level = levelParam instanceof Integer ? (Integer) levelParam : Integer.parseInt(levelParam.toString());
                
                Map<String, Object> experienceData = new HashMap<>();
                experienceData.put("current_xp", xp);
                experienceData.put("level", level);
                userExperience.put(userId, experienceData);
                
                resultSet = new StubResultSet(userExperience, userId);
                return 1;
            } else if (sql.toLowerCase().contains("users")) {
                // Handle INSERT INTO users (email, password) VALUES (?, ?)
                String email = (String) parameters[0];
                String password = (String) parameters[1];
                
                // Check if user already exists
                for (Map.Entry<Integer, Map<String, Object>> entry : users.entrySet()) {
                    if (email.equals(entry.getValue().get("email"))) {
                        throw new SQLException("User with email " + email + " already exists");
                    }
                }
                
                // Add new user
                StubDatabaseConfig dbConfig = StubDatabaseConfig.getInstance();
                int newId = dbConfig.getNextUserId();
                Map<String, Object> userData = new HashMap<>();
                userData.put("email", email);
                userData.put("password", password);
                userData.put("balance", 0.0);
                userData.put("points", 0);
                users.put(newId, userData);
                dbConfig.incrementNextUserId();
                
                // Set the generated key in the result set
                resultSet = new StubResultSet(users, newId);
                return 1;
            }
        } else if (sql.toLowerCase().contains("delete")) {
            if (sql.toLowerCase().contains("user_experience")) {
                // Handle DELETE FROM user_experience WHERE user_id = ?
                Object param = parameters[0];
                if (param == null) {
                    throw new SQLException("User ID cannot be null");
                }
                int userId = param instanceof Integer ? (Integer) param : Integer.parseInt(param.toString());
                if (userExperience.remove(userId) != null) {
                    return 1;
                }
            } else if (sql.toLowerCase().contains("users")) {
                // Handle DELETE FROM users WHERE id = ?
                Object param = parameters[0];
                if (param == null) {
                    throw new SQLException("User ID cannot be null");
                }
                int userId = param instanceof Integer ? (Integer) param : Integer.parseInt(param.toString());
                if (users.remove(userId) != null) {
                    return 1;
                }
            }
        } else if (sql.toLowerCase().contains("update")) {
            if (sql.toLowerCase().contains("user_experience")) {
                // Handle UPDATE user_experience SET current_xp = ?, level = ? WHERE user_id = ?
                Object xpParam = parameters[0];
                Object levelParam = parameters[1];
                Object userIdParam = parameters[2];
                
                if (userIdParam == null || xpParam == null || levelParam == null) {
                    throw new SQLException("Parameters cannot be null");
                }
                
                int userId = userIdParam instanceof Integer ? (Integer) userIdParam : Integer.parseInt(userIdParam.toString());
                int xp = xpParam instanceof Integer ? (Integer) xpParam : Integer.parseInt(xpParam.toString());
                int level = levelParam instanceof Integer ? (Integer) levelParam : Integer.parseInt(levelParam.toString());
                
                Map<String, Object> experienceData = userExperience.get(userId);
                if (experienceData != null) {
                    experienceData.put("current_xp", xp);
                    experienceData.put("level", level);
                    return 1;
                }
            } else if (sql.toLowerCase().contains("users")) {
                // Handle UPDATE users SET ... WHERE id = ?
                Object idParam = parameters[parameters.length - 1];
                if (idParam == null) {
                    throw new SQLException("User ID cannot be null");
                }
                int userId = idParam instanceof Integer ? (Integer) idParam : Integer.parseInt(idParam.toString());
                Map<String, Object> userData = users.get(userId);
                if (userData != null) {
                    if (sql.toLowerCase().contains("password")) {
                        userData.put("password", parameters[0]);
                    } else if (sql.toLowerCase().contains("balance")) {
                        Object balanceParam = parameters[0];
                        if (balanceParam != null) {
                            double balance = balanceParam instanceof Double ? (Double) balanceParam : Double.parseDouble(balanceParam.toString());
                            userData.put("balance", balance);
                        }
                    } else if (sql.toLowerCase().contains("points")) {
                        Object pointsParam = parameters[0];
                        if (pointsParam != null) {
                            int points = pointsParam instanceof Integer ? (Integer) pointsParam : Integer.parseInt(pointsParam.toString());
                            userData.put("points", points);
                        }
                    }
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        parameters[parameterIndex - 1] = null;
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setBigDecimal(int parameterIndex, java.math.BigDecimal x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setAsciiStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setUnicodeStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setBinaryStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void clearParameters() throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = null;
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public boolean execute() throws SQLException {
        if (sql.toLowerCase().contains("select")) {
            resultSet = executeQuery();
            return true;
        } else {
            executeUpdate();
            return false;
        }
    }

    @Override
    public void addBatch() throws SQLException {
        // No-op for stub
    }

    @Override
    public void setCharacterStream(int parameterIndex, java.io.Reader reader, int length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        parameters[parameterIndex - 1] = null;
    }

    @Override
    public void setURL(int parameterIndex, java.net.URL x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        parameters[parameterIndex - 1] = value;
    }

    @Override
    public void setNCharacterStream(int parameterIndex, java.io.Reader value, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setClob(int parameterIndex, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setBlob(int parameterIndex, java.io.InputStream inputStream, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setNClob(int parameterIndex, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        parameters[parameterIndex - 1] = x;
    }

    @Override
    public void setAsciiStream(int parameterIndex, java.io.InputStream x, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setBinaryStream(int parameterIndex, java.io.InputStream x, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setCharacterStream(int parameterIndex, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setAsciiStream(int parameterIndex, java.io.InputStream x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setBinaryStream(int parameterIndex, java.io.InputStream x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setCharacterStream(int parameterIndex, java.io.Reader reader) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setNCharacterStream(int parameterIndex, java.io.Reader value) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setClob(int parameterIndex, java.io.Reader reader) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setBlob(int parameterIndex, java.io.InputStream inputStream) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void setNClob(int parameterIndex, java.io.Reader reader) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        if (sql.toLowerCase().contains("insert") && sql.toLowerCase().contains("users")) {
            // For user registration, return the newly created user's ID
            StubDatabaseConfig dbConfig = StubDatabaseConfig.getInstance();
            int newId = dbConfig.getNextUserId() - 1;
            resultSet = new StubResultSet(users, newId);
            return resultSet;
        }
        return new StubResultSet(users);
    }
} 