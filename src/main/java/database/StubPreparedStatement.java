package database;

import java.sql.*;
import java.util.Map;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class StubPreparedStatement extends StubStatement implements PreparedStatement {
    private final String sql;
    private final Object[] parameters;
    private final Map<Integer, Map<String, Object>> users;
    private final Map<Integer, Map<String, Object>> userExperience;
    private final Map<Integer, Map<String, Object>> transactions;

    public StubPreparedStatement(Map<Integer, Map<String, Object>> users, Map<Integer, Map<String, Object>> userExperience, String sql) {
        super(users, userExperience);
        this.sql = sql;
        this.parameters = new Object[10]; // Support up to 10 parameters
        this.users = users;  // Initialize the final users field
        this.userExperience = userExperience;
        this.transactions = new HashMap<>();  // Initialize an empty transactions map
    }

    public StubPreparedStatement(Map<Integer, Map<String, Object>> users, Map<Integer, Map<String, Object>> userExperience, Map<Integer, Map<String, Object>> transactions, String sql) {
        super(users, userExperience);
        this.sql = sql;
        this.parameters = new Object[10]; // Support up to 10 parameters
        this.users = users;
        this.userExperience = userExperience;
        this.transactions = transactions != null ? transactions : new HashMap<>();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        if (sql.toLowerCase().contains("select")) {
            if (sql.toLowerCase().contains("transactions")) {
                if (sql.toLowerCase().contains("sum") && sql.toLowerCase().contains("category")) {
                    // Handle SELECT SUM(amount) as total FROM transactions WHERE user_id = ? AND category = ?
                    Object userIdParam = parameters[0];
                    Object categoryParam = parameters[1];
                    
                    if (userIdParam == null || categoryParam == null) {
                        throw new SQLException("Parameters cannot be null");
                    }
                    
                    int userId = userIdParam instanceof Integer ? (Integer) userIdParam : Integer.parseInt(userIdParam.toString());
                    String category = (String) categoryParam;
                    
                    System.out.println("Calculating total for category: " + category + " for user: " + userId);
                    
                    // Calculate the sum for the given category
                    double total = 0.0;
                    for (Map.Entry<Integer, Map<String, Object>> entry : transactions.entrySet()) {
                        Map<String, Object> transaction = entry.getValue();
                        if (transaction.containsKey("user_id") && 
                            transaction.get("user_id").equals(userId) &&
                            transaction.containsKey("category") &&
                            transaction.get("category").equals(category) &&
                            transaction.containsKey("amount")) {
                            
                            double amount = (Double) transaction.get("amount");
                            total += amount;
                            System.out.println("Adding amount: " + amount + " to total: " + total);
                        }
                    }
                    
                    // Create a new map with just the total
                    Map<Integer, Map<String, Object>> resultData = new HashMap<>();
                    Map<String, Object> totalData = new HashMap<>();
                    totalData.put("total", total);
                    resultData.put(1, totalData);
                    
                    System.out.println("Final category total: " + total);
                    resultSet = new StubResultSet(resultData);
                    return resultSet;
                } else if (sql.toLowerCase().contains("user_id = ?")) {
                    // Handle SELECT * FROM transactions WHERE user_id = ?
                    Object param = parameters[0];
                    if (param == null) {
                        throw new SQLException("User ID cannot be null");
                    }
                    int userId = param instanceof Integer ? (Integer) param : Integer.parseInt(param.toString());
                    
                    // Filter transactions for the given user
                    Map<Integer, Map<String, Object>> userTransactions = new HashMap<>();
                    for (Map.Entry<Integer, Map<String, Object>> entry : transactions.entrySet()) {
                        Map<String, Object> transaction = entry.getValue();
                        if (transaction.containsKey("user_id") && 
                            transaction.get("user_id").equals(userId)) {
                            userTransactions.put(entry.getKey(), transaction);
                        }
                    }
                    
                    resultSet = new StubResultSet(userTransactions);
                    return resultSet;
                } else if (sql.toLowerCase().contains("date >= ?") && sql.toLowerCase().contains("date < ?")) {
                    // Handle SELECT * FROM transactions WHERE user_id = ? AND date >= ? AND date < ?
                    Object userIdParam = parameters[0];
                    Object startDateParam = parameters[1];
                    Object endDateParam = parameters[2];
                    
                    if (userIdParam == null || startDateParam == null || endDateParam == null) {
                        throw new SQLException("Parameters cannot be null");
                    }
                    
                    int userId = userIdParam instanceof Integer ? (Integer) userIdParam : Integer.parseInt(userIdParam.toString());
                    java.sql.Date startDate = (java.sql.Date) startDateParam;
                    java.sql.Date endDate = (java.sql.Date) endDateParam;
                    
                    System.out.println("Filtering transactions for userId=" + userId + " between " + startDate + " and " + endDate);
                    System.out.println("Total transactions in map: " + transactions.size());
                    
                    // Create a new result set with the filtered transactions
                    Map<Integer, Map<String, Object>> filteredTransactions = new HashMap<>();
                    for (Map.Entry<Integer, Map<String, Object>> entry : transactions.entrySet()) {
                        Map<String, Object> transaction = entry.getValue();
                        if (transaction.containsKey("user_id") && 
                            transaction.get("user_id").equals(userId) &&
                            transaction.containsKey("date")) {
                            java.sql.Date transactionDate = (java.sql.Date) transaction.get("date");
                            
                            // Debug information
                            System.out.println("Checking transaction: ID=" + entry.getKey() + 
                                          ", User=" + transaction.get("user_id") + 
                                          ", Date=" + transactionDate + 
                                          ", Description=" + transaction.get("description"));
                            
                            // Date comparison
                            boolean inRange = transactionDate.compareTo(startDate) >= 0 && transactionDate.compareTo(endDate) < 0;
                            System.out.println("Date comparison: " + transactionDate + " >= " + startDate + " ? " + 
                                          (transactionDate.compareTo(startDate) >= 0) + 
                                          " AND " + transactionDate + " < " + endDate + " ? " + 
                                          (transactionDate.compareTo(endDate) < 0) + 
                                          " In range? " + inRange);
                            
                            if (inRange) {
                                filteredTransactions.put(entry.getKey(), transaction);
                                System.out.println("Found matching transaction: " + entry.getKey() + " on " + transactionDate + 
                                             ", User=" + transaction.get("user_id") + 
                                             ", Description=" + transaction.get("description"));
                            }
                        }
                    }
                    
                    resultSet = new StubResultSet(filteredTransactions);
                    return resultSet;
                }
            } else if (sql.toLowerCase().contains("user_experience")) {
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
                } else if (sql.toLowerCase().contains("monthly_budget")) {
                    // Handle SELECT monthly_budget FROM users WHERE id = ?
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
                } else if (sql.toLowerCase().contains("user_budget")) {
                    // Handle SELECT total_budget FROM user_budget WHERE user_id = ?
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
                }
            }
        }
        throw new SQLException("Unsupported query: " + sql);
    }

    @Override
    public int executeUpdate() throws SQLException {
        if (sql.toLowerCase().contains("insert")) {
            if (sql.toLowerCase().contains("transactions")) {
                // Handle INSERT INTO transactions (user_id, date, description, category, type, amount) VALUES (?, ?, ?, ?, ?, ?)
                Object userIdParam = parameters[0];
                Object dateParam = parameters[1];
                Object descriptionParam = parameters[2];
                Object categoryParam = parameters[3];
                Object typeParam = parameters[4];
                Object amountParam = parameters[5];
                
                if (userIdParam == null || dateParam == null || descriptionParam == null || 
                    categoryParam == null || typeParam == null || amountParam == null) {
                    throw new SQLException("All transaction parameters must be non-null");
                }
                
                int userId = userIdParam instanceof Integer ? (Integer) userIdParam : Integer.parseInt(userIdParam.toString());
                java.sql.Date date;
                try {
                    if (dateParam instanceof java.sql.Date) {
                        date = (java.sql.Date) dateParam;
                    } else if (dateParam instanceof java.time.LocalDate) {
                        date = java.sql.Date.valueOf((java.time.LocalDate) dateParam);
                    } else if (dateParam instanceof String) {
                        date = java.sql.Date.valueOf((String) dateParam);
                    } else {
                        throw new SQLException("Invalid date format: " + dateParam.getClass().getName());
                    }
                } catch (IllegalArgumentException e) {
                    throw new SQLException("Invalid date value: " + dateParam, e);
                }
                String description = (String) descriptionParam;
                String category = (String) categoryParam;
                String type = (String) typeParam;
                double amount = amountParam instanceof Double ? (Double) amountParam : Double.parseDouble(amountParam.toString());
                
                // Create new transaction
                Map<String, Object> transactionData = new HashMap<>();
                transactionData.put("user_id", userId);
                transactionData.put("date", date);
                transactionData.put("description", description);
                transactionData.put("category", category);
                transactionData.put("type", type);
                transactionData.put("amount", amount);
                
                // Add to transactions map with a unique ID
                int newId = transactions.size() + 1;
                transactions.put(newId, transactionData);
                
                System.out.println("Added transaction: ID=" + newId + ", User=" + userId + ", Date=" + date + ", Description=" + description + ", Amount=" + amount);
                System.out.println("Total transactions in map: " + transactions.size());
                System.out.println("All transaction IDs: " + transactions.keySet());
                
                return 1;
            } else if (sql.toLowerCase().contains("user_experience")) {
                // Handle INSERT INTO user_experience (user_id, current_xp, level) VALUES (?, ?, ?)
                Object userIdParam = parameters[0];
                Object xpParam = parameters[1];
                Object levelParam = parameters[2];
                
                // Check if any parameter is null
                if (userIdParam == null) {
                    throw new SQLException("User ID cannot be null");
                }
                if (xpParam == null) {
                    xpParam = 0;  // Default to 0 XP
                }
                if (levelParam == null) {
                    levelParam = 1;  // Default to level 1
                }
                
                int userId = userIdParam instanceof Integer ? (Integer) userIdParam : Integer.parseInt(userIdParam.toString());
                int xp = xpParam instanceof Integer ? (Integer) xpParam : Integer.parseInt(xpParam.toString());
                int level = levelParam instanceof Integer ? (Integer) levelParam : Integer.parseInt(levelParam.toString());
                
                // Check if user exists before adding experience
                if (!users.containsKey(userId)) {
                    throw new SQLException("Cannot add experience for non-existent user: " + userId);
                }
                
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
                
                // Store the generated key for later retrieval
                Map<String, Object> generatedKeys = new HashMap<>();
                generatedKeys.put("id", newId);
                users.put(-1, generatedKeys);  // Store at special index for retrieval
                
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
            } else if (sql.toLowerCase().contains("transactions")) {
                // Handle DELETE FROM transactions WHERE id = ?
                Object param = parameters[0];
                if (param == null) {
                    throw new SQLException("Transaction ID cannot be null");
                }
                int transactionId = param instanceof Integer ? (Integer) param : Integer.parseInt(param.toString());
                System.out.println("Attempting to delete transaction with ID: " + transactionId);
                if (transactions.remove(transactionId) != null) {
                    System.out.println("Successfully deleted transaction with ID: " + transactionId);
                    System.out.println("Total transactions remaining: " + transactions.size());
                    System.out.println("Remaining transaction IDs: " + transactions.keySet());
                    return 1;
                } else {
                    System.out.println("Transaction with ID: " + transactionId + " not found");
                    System.out.println("Available transaction IDs: " + transactions.keySet());
                    return 0;
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
                
                System.out.println("Updating experience for user " + userId + ": XP=" + xp + ", Level=" + level);
                
                Map<String, Object> experienceData = userExperience.get(userId);
                if (experienceData != null) {
                    experienceData.put("current_xp", xp);
                    experienceData.put("level", level);
                    
                    // Also update points in users table
                    Map<String, Object> userData = users.get(userId);
                    if (userData != null) {
                        userData.put("points", xp);
                        System.out.println("Also updated points for user " + userId + " to " + xp);
                    } else {
                        System.out.println("WARNING: User " + userId + " not found in users map when updating experience");
                    }
                    
                    return 1;
                } else {
                    System.out.println("WARNING: User " + userId + " not found in experience map when updating experience");
                }
            } else if (sql.toLowerCase().contains("users")) {
                // Handle UPDATE users SET ... WHERE id = ?
                if (sql.toLowerCase().contains("password")) {
                    // For password update: UPDATE users SET password = ? WHERE id = ?
                    Object passwordParam = parameters[0];
                    Object idParam = parameters[1];
                    if (idParam == null) {
                        throw new SQLException("User ID cannot be null");
                    }
                    if (passwordParam == null) {
                        throw new SQLException("Password cannot be null");
                    }
                    int userId = idParam instanceof Integer ? (Integer) idParam : Integer.parseInt(idParam.toString());
                    Map<String, Object> userData = users.get(userId);
                    if (userData != null) {
                        userData.put("password", passwordParam);
                        return 1;
                    }
                } else if (sql.toLowerCase().contains("balance")) {
                    // Handle both "balance = ?" and "balance = balance + ?" forms
                    Object balanceParam = parameters[0];
                    Object idParam = parameters[1];
                    if (idParam == null) {
                        throw new SQLException("User ID cannot be null");
                    }
                    if (balanceParam != null) {
                        int userId = idParam instanceof Integer ? (Integer) idParam : Integer.parseInt(idParam.toString());
                        Map<String, Object> userData = users.get(userId);
                        if (userData != null) {
                            double newBalance;
                            if (sql.toLowerCase().contains("balance + ?")) {
                                // Incremental update: balance = balance + ?
                                double currentBalance = userData.containsKey("balance") ? 
                                    (userData.get("balance") instanceof Double ? (Double) userData.get("balance") : 
                                    Double.parseDouble(userData.get("balance").toString())) : 0.0;
                                double increment = balanceParam instanceof Double ? (Double) balanceParam : 
                                    Double.parseDouble(balanceParam.toString());
                                newBalance = currentBalance + increment;
                                System.out.println("DEBUG: Incrementing balance for user " + userId + " by " + increment + 
                                    " from " + currentBalance + " to " + newBalance);
                            } else {
                                // Absolute update: balance = ?
                                newBalance = balanceParam instanceof Double ? (Double) balanceParam : 
                                    Double.parseDouble(balanceParam.toString());
                                System.out.println("DEBUG: Setting balance for user " + userId + " to " + newBalance);
                            }
                            userData.put("balance", newBalance);
                            return 1;
                        }
                    }
                } else if (sql.toLowerCase().contains("points")) {
                    // Handle both UPDATE users SET points = ? WHERE id = ? 
                    // and UPDATE users SET points = points + ? WHERE id = ?
                    Object pointsParam = parameters[0];
                    Object idParam = parameters[1];
                    if (idParam == null) {
                        throw new SQLException("User ID cannot be null");
                    }
                    if (pointsParam != null) {
                        int userId = idParam instanceof Integer ? (Integer) idParam : Integer.parseInt(idParam.toString());
                        Map<String, Object> userData = users.get(userId);
                        if (userData != null) {
                            int points;
                            if (sql.toLowerCase().contains("points + ?")) {
                                // Incremental update: points = points + ?
                                int currentPoints = userData.containsKey("points") ? (Integer) userData.get("points") : 0;
                                int increment = pointsParam instanceof Integer ? (Integer) pointsParam : Integer.parseInt(pointsParam.toString());
                                points = currentPoints + increment;
                                System.out.println("DEBUG: Incrementing points for user " + userId + " by " + increment + " from " + currentPoints + " to " + points);
                            } else {
                                // Absolute update: points = ?
                                points = pointsParam instanceof Integer ? (Integer) pointsParam : Integer.parseInt(pointsParam.toString());
                                System.out.println("DEBUG: Setting points for user " + userId + " to " + points);
                            }
                            userData.put("points", points);
                            return 1;
                        }
                    }
                } else if (sql.toLowerCase().contains("monthly_budget")) {
                    Object budgetParam = parameters[0];
                    Object idParam = parameters[1];
                    if (idParam == null) {
                        throw new SQLException("User ID cannot be null");
                    }
                    if (budgetParam != null) {
                        int userId = idParam instanceof Integer ? (Integer) idParam : Integer.parseInt(idParam.toString());
                        Map<String, Object> userData = users.get(userId);
                        if (userData == null) {
                            userData = new HashMap<>();
                            users.put(userId, userData);
                        }
                        double budget = budgetParam instanceof Double ? (Double) budgetParam : Double.parseDouble(budgetParam.toString());
                        userData.put("monthly_budget", budget);
                        return 1;
                    }
                } else if (sql.toLowerCase().contains("user_budget")) {
                    // Handle INSERT INTO user_budget (user_id, total_budget) VALUES (?, ?) ON CONFLICT (user_id) DO UPDATE SET total_budget = ?
                    Object userIdParam = parameters[0];
                    Object budgetParam = parameters[1];
                    if (userIdParam == null) {
                        throw new SQLException("User ID cannot be null");
                    }
                    if (budgetParam == null) {
                        throw new SQLException("Budget cannot be null");
                    }
                    int userId = userIdParam instanceof Integer ? (Integer) userIdParam : Integer.parseInt(userIdParam.toString());
                    double budget = budgetParam instanceof Double ? (Double) budgetParam : Double.parseDouble(budgetParam.toString());
                    
                    Map<String, Object> userData = users.get(userId);
                    if (userData == null) {
                        userData = new HashMap<>();
                        users.put(userId, userData);
                    }
                    userData.put("total_budget", budget);
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
            Map<String, Object> generatedKeys = users.remove(-1);  // Remove from special index
            if (generatedKeys != null) {
                int newId = (Integer) generatedKeys.get("id");
                return new StubResultSet(users, newId);
            }
        }
        return new StubResultSet(users);
    }
} 