-- init.sql

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

-- Create transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create user_budget table
CREATE TABLE IF NOT EXISTS user_budget (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    total_budget DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create quests table
CREATE TABLE IF NOT EXISTS quests (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    quest_type VARCHAR(50) NOT NULL,
    xp_reward INTEGER NOT NULL,
    required_amount DOUBLE PRECISION,
    completion_status BOOLEAN NOT NULL DEFAULT FALSE,
    deadline DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create user_experience table
CREATE TABLE IF NOT EXISTS user_experience (
    user_id INTEGER PRIMARY KEY,
    current_xp INTEGER NOT NULL DEFAULT 0,
    level INTEGER NOT NULL DEFAULT 1,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Optional seed data:
-- Insert default budgets for users who don't have one
INSERT INTO user_budget (user_id, total_budget)
SELECT id, 0.0 FROM users u
WHERE NOT EXISTS (SELECT 1 FROM user_budget ub WHERE ub.user_id = u.id);

-- Insert default user experience for users who don't have one
INSERT INTO user_experience (user_id, current_xp, level)
SELECT id, 0, 1 FROM users u
WHERE NOT EXISTS (SELECT 1 FROM user_experience ue WHERE ue.user_id = u.id);
