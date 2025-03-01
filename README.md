# Financial Budget Gamified App

A Java-based financial budgeting application with gamification features.

## Prerequisites

- Java JDK 18 or higher
- Maven (for dependency management)
- MySQL Server (8.0 or higher)

## Database Setup

1. **Start MySQL Server**
   - Make sure your MySQL server is running
   - On Windows: Check Services app or run `net start mysql`
   - On Mac: `brew services start mysql` or `sudo mysql.server start`
   - On Linux: `sudo systemctl start mysql`

2. **Create Database and Tables**
   - Open MySQL command line:
     ```bash
     mysql -u root -p
     ```
   - Enter your MySQL root password when prompted
   - Run the following SQL commands:

     ```sql
     -- Create database
     CREATE DATABASE financial_budget_gamified;
     
     -- Use the database
     USE financial_budget_gamified;
     
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

3. **Create Database User**
   - Create a dedicated user for the application (safer than using root):
     ```sql
     CREATE USER 'fbg_user'@'localhost' IDENTIFIED BY 'fbg_password';
     GRANT ALL PRIVILEGES ON financial_budget_gamified.* TO 'fbg_user'@'localhost';
     FLUSH PRIVILEGES;
     ```

4. **Configure Database Connection**
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
   - Check if MySQL service is active
   - Try connecting via command line: `mysql -u fbg_user -p`

2. **Check Connection Parameters**
   - Verify the database name, username, and password in `DatabaseManager.java`
   - Default MySQL port is 3306 - make sure it's not changed

3. **Common Connection Issues**
   - MySQL not running
   - Incorrect database name, username, or password
   - MySQL server not allowing connections (check `bind-address` in my.cnf/my.ini)
   - Firewall blocking port 3306

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
