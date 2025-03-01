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