<<<<<<< HEAD
# Financial Budget Gamified App

A Java-based financial budgeting application with gamification features.

## Prerequisites

- Java JDK 18 or higher
- Maven (for dependency management)
- MySQL Server (8.0 or higher)
- MySQL Workbench (for database management)

## Database Setup with MySQL Workbench

1. **Start MySQL Server**
   - Make sure your MySQL server is running
   - On Windows: Check Services app or MySQL Notifier in system tray
   - On Mac: Check Activity Monitor or system preferences
   - On Linux: `sudo systemctl status mysql`

2. **Open MySQL Workbench**
   - Launch MySQL Workbench application
   - Connect to your MySQL server instance (typically localhost)
   - Enter your root password if prompted

3. **Create Database and Tables**
   - In MySQL Workbench, click on the "Create a new schema" button (database icon with a + sign)
   - Name it `financial_budget_gamified` and click Apply
   - After creation, double-click on the new schema to make it the active schema
   - Click on the "SQL Editor" button (SQL file icon) to open a new query tab
   - Copy and paste the following SQL code:

     ```sql
     -- Create users table
     CREATE TABLE users (
         id INT AUTO_INCREMENT PRIMARY KEY,
         name VARCHAR(100) NOT NULL,
         email VARCHAR(100) NOT NULL,
         initial_balance DOUBLE NOT NULL,
         xp INT DEFAULT 0,
         creation_date DATETIME DEFAULT CURRENT_TIMESTAMP
     );
     
     -- Create transactions table
     CREATE TABLE transactions (
         id INT AUTO_INCREMENT PRIMARY KEY,
         description VARCHAR(255) NOT NULL,
         amount DOUBLE NOT NULL,
         date DATE NOT NULL,
         is_income BOOLEAN NOT NULL,
         category VARCHAR(100) DEFAULT 'Other',
         user_id INT,
         FOREIGN KEY (user_id) REFERENCES users(id)
     );
     
     -- Optional: Create a test user
     INSERT INTO users (name, email, initial_balance) VALUES ('Test User', 'test@example.com', 1000.00);
     ```

   - Click the lightning bolt icon (or press Ctrl+Shift+Enter) to execute the entire script
   - Verify tables were created by refreshing the schema (right-click on the schema and select "Refresh All")

4. **Create Database User**
   - In MySQL Workbench, go to the "Administration" tab
   - Click "Users and Privileges"
   - Click "Add Account"
   - Set the following:
     - Login Name: `fbg_user`
     - Password: `fbg_password`
     - Confirm Password: `fbg_password`
   - Under "Schema Privileges" tab, click "Add Entry"
   - Select "Selected schema" and choose `financial_budget_gamified`
   - Check "ALL" privileges
   - Click "Apply"

5. **Configure Database Connection**
   - Locate the `DatabaseManager.java` file in your project
   - Ensure the connection details match your setup:

     ```java
     // Example connection string
     private static final String URL = "jdbc:mysql://localhost:3306/financial_budget_gamified";
     private static final String USER = "fbg_user";
     private static final String PASSWORD = "fbg_password";
     ```

## Application Setup

1. **Clone the Repository**
   ```bash
   git clone [repository-url]
   cd EECS2311_FinancialBudgetGamified-App
   ```

2. **Build the Project**
   ```bash
   mvn clean install
   ```

3. **Run the Application**
   ```bash
   mvn exec:java -Dexec.mainClass="view.CalendarUI"
   ```
   
   Alternatively, you can run the JAR file directly:
   ```bash
   java -jar target/EECS2311_FinancialBudgetGamified-App-1.0-SNAPSHOT.jar
   ```

## Troubleshooting Database Connection

If you encounter database connection issues:

1. **Verify MySQL is Running**
   - Open MySQL Workbench and check if you can connect to your instance
   - Try connecting with the `fbg_user` credentials

2. **Check Connection Parameters**
   - Verify the database name, username, and password in `DatabaseManager.java`
   - Default MySQL port is 3306 - make sure it's not changed

3. **Common Connection Issues**
   - Test the connection in MySQL Workbench with the same credentials
   - Check if MySQL server allows remote connections
   - Verify server hostname if not using localhost

4. **Debug Connection String**
   - Try this connection URL format with explicit parameters:
     ```
     jdbc:mysql://localhost:3306/financial_budget_gamified?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
     ```

## Dependencies

The project uses the following main dependencies:
- MySQL Connector (8.0.33) - For database connectivity
- JavaMail API (1.6.2) - For email functionality
- JUnit 5 (5.9.3) - For testing

## Support

For any issues or questions, please open an issue in the repository.
=======
# EECS2311_ProjectName
ITR0: 
- deadline: Jan 29, WED
- First meeting with a customer on Jan 25th, 2025
>>>>>>> parent of 92c92e1 (Added initial UI)
