CREATE DATABASE budget_app;

USE budget_app;

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    date DATE NOT NULL,
    is_income BOOLEAN NOT NULL
);
