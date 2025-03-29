    package controller;

    import database.DatabaseManager;
    import model.Transaction;
    import java.sql.*;
    import java.time.LocalDate;
    import java.util.ArrayList;
    import java.util.List;

    public class TransactionController {
        private Connection connection;
        private int userId = 1; // Default to 1 if not set explicitly
        private static final int XP_REWARD_PER_TRANSACTION = 20; // XP points awarded for logging a transaction

        public TransactionController(Connection connection) {
            this.connection = connection;
        }

        /**
         * Updates a user's balance in the database
         * @param userId The ID of the user
         * @param amount The amount to update the balance by (positive for increase, negative for decrease)
         * @return true if the balance was updated successfully, false otherwise
         */
        public boolean updateBalance(int userId, double amount) {
            String query = "UPDATE users SET balance = balance + ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, userId);
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Adds a new transaction for the given user.
         *
         * @param userId      the user ID
         * @param date        the date in ISO format (YYYY-MM-DD)
         * @param description a description of the transaction
         * @param category    the transaction category
         * @param type        the transaction type (e.g., "income" or "expense")
         * @param amount      the transaction amount
         * @return true if the transaction was successfully added, false otherwise
         */
        public static boolean addTransaction(int userId, String date, String description, String category, String type, double amount) {
            if(amount < 0.00) {
                throw new IllegalArgumentException("Amount can not be negative");
            }
            if(description == null) {
                throw new NullPointerException("Description can not be null");
            }
            if(description.length() <= 0) {
                throw new IllegalArgumentException("Description can not be empty");
            }
            if(type == null) {
                throw new NullPointerException("Type can not be null");
            }
            if(date == null) {
                throw new NullPointerException("Date can not be null");
            }
            String query = "INSERT INTO transactions (user_id, date, description, category, type, amount) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setDate(2, java.sql.Date.valueOf(date));
                pstmt.setString(3, description);
                pstmt.setString(4, category);
                pstmt.setString(5, type);
                pstmt.setDouble(6, amount);
                pstmt.executeUpdate();
                
                // Award XP for logging a transaction
                awardXpForTransaction(userId);
                
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        // ADD: Add Transaction without category (for backwards compatibility)
    //    public void addTransaction(String description, double amount, LocalDate date, boolean isIncome) {
    //        addTransaction(description, amount, date, isIncome, "Other");
    //    }
        
        // Set the user ID for transactions
        public void setUserId(int userId) {
            this.userId = userId;
        }

        // ADD: Add Transaction with category
        public void addTransaction(String description, double amount, LocalDate date, boolean isIncome, String category) {
            String query = "INSERT INTO transactions (user_id, description, amount, date, type, category) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId); // Use the stored userId
                stmt.setString(2, description);
                stmt.setDouble(3, amount);
                stmt.setDate(4, Date.valueOf(date));
                stmt.setString(5, isIncome ? "income" : "expense");
                stmt.setString(6, category);
                stmt.executeUpdate();
                
                // Award XP for logging a transaction
                awardXpForTransaction(userId);
                
                System.out.println("Transaction added successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to add transaction!");
            }
        }

        // READ: Fetch all transactions for the current user
        public List<Transaction> getTransactions() {
            List<Transaction> transactions = new ArrayList<>();
            String query = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String category = "Other";
                        try {
                            category = rs.getString("category");
                            if (category == null) category = "Other";
                        } catch (SQLException e) {
                            // Column might not exist in older database versions
                            category = "Other";
                        }
                        
                        String type = rs.getString("type");
                        boolean isIncome = type != null && type.equalsIgnoreCase("income");
                        
                        // Use the appropriate constructor based on the database schema
                        Transaction transaction = new Transaction(
                                rs.getInt("id"),
                                userId,
                                rs.getTimestamp("date"),
                                rs.getString("description"),
                                category,
                                type,
                                rs.getDouble("amount")
                        );
                        transactions.add(transaction);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to fetch transactions!");
            }
            return transactions;
        }

        // DELETE: Remove a transaction by ID
        public static boolean deleteTransaction(int transactionId) {
            String query = "DELETE FROM transactions WHERE id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, transactionId);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Retrieves all transactions for the given user from the database.
         *
         * @param userId the user ID
         * @return a List of Transaction objects
         */
        public static List<Transaction> getAllTransactions(int userId) {
            List<Transaction> transactions = new ArrayList<>();
            String query = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC";
            try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Transaction t = new Transaction(
                                rs.getInt("id"),
                                userId,
                                rs.getTimestamp("date"),
                                rs.getString("description"),
                                rs.getString("category"),
                                rs.getString("type"),
                                rs.getDouble("amount")
                        );
                        transactions.add(t);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return transactions;
        }

        /**
         * Gets all transactions for a user with the specified category and within the date range
         * @param userId The ID of the user
         * @param category The category to filter by
         * @param startDate The start date of the range
         * @param endDate The end date of the range
         * @return A list of matching transactions
         */
        public List<Transaction> getTransactionsByCategoryAndDateRange(int userId, String category, 
                                                                    java.util.Date startDate, java.util.Date endDate) throws SQLException {
            List<Transaction> transactions = new ArrayList<>();
            String sql = "SELECT * FROM transactions WHERE user_id = ? AND category = ? AND date BETWEEN ? AND ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setString(2, category);
                stmt.setDate(3, new java.sql.Date(startDate.getTime()));
                stmt.setDate(4, new java.sql.Date(endDate.getTime()));
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String description = rs.getString("description");
                        double amount = rs.getDouble("amount");
                        java.sql.Date date = rs.getDate("date");
                        String type = rs.getString("type");
                        String transCategory = rs.getString("category");
                        
                        LocalDate localDate = date.toLocalDate();
                        boolean isIncome = type.equalsIgnoreCase("income");
                        
                        Transaction transaction = new Transaction(description, amount, localDate, isIncome, transCategory);
                        transaction.setId(id);
                        transactions.add(transaction);
                    }
                }
            }
            
            return transactions;
        }

        /**
         * Gets transactions for a specific month
         * @param userId The ID of the user
         * @param year The year to get transactions for
         * @param month The month to get transactions for (1-12)
         * @return List of transactions for the specified month
         */
        public List<Transaction> getTransactionsByMonth(int userId, int year, int month) {
            List<Transaction> transactions = new ArrayList<>();
            String query = "SELECT * FROM transactions WHERE user_id = ? AND EXTRACT(YEAR FROM date) = ? AND EXTRACT(MONTH FROM date) = ? ORDER BY date DESC";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, year);
                pstmt.setInt(3, month);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Transaction t = new Transaction(
                                rs.getInt("id"),
                                userId,
                                rs.getTimestamp("date"),
                                rs.getString("description"),
                                rs.getString("category"),
                                rs.getString("type"),
                                rs.getDouble("amount")
                        );
                        transactions.add(t);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return transactions;
        }

    /**
     * Awards XP to a user for logging a transaction and checks for quest completion
     */
    public static void awardXpForTransaction(int userId) {
        try {
            QuestController questController = new QuestController();
            // Award XP for the transaction
            questController.addUserXP(userId, XP_REWARD_PER_TRANSACTION);
            System.out.println("Awarded " + XP_REWARD_PER_TRANSACTION + " XP for logging a transaction");
            
            // Check and complete quests automatically
            questController.checkAndCompleteQuests(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to award XP for transaction!");
        }
    }

    /**
     * Gets the total amount for a specific category
     * @param userId The ID of the user
     * @param category The category to get the total for
     * @return The total amount for the category
     */
    public double getCategoryTotal(int userId, String category) {
        String query = "SELECT SUM(amount) as total FROM transactions WHERE user_id = ? AND category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, category);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
