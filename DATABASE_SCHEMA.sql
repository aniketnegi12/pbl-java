-- Database Schema for PBL Java Pricing System
-- Last Updated: 28 May 2026

-- ============================================
-- CREATE DATABASE
-- ============================================

CREATE DATABASE IF NOT EXISTS pbl_database;
USE pbl_database;

-- ============================================
-- TABLES
-- ============================================

-- 1. Products Table
CREATE TABLE IF NOT EXISTS products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(100) NOT NULL DEFAULT 'General',
    cost_price DECIMAL(10, 2) NOT NULL CHECK(cost_price >= 0),
    competitor_price DECIMAL(10, 2) CHECK(competitor_price >= 0),
    suggested_price DECIMAL(10, 2),
    profit_margin DECIMAL(5, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_name (product_name)
);

-- 2. Pricing History Table
CREATE TABLE IF NOT EXISTS pricing_history (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    old_price DECIMAL(10, 2),
    new_price DECIMAL(10, 2) NOT NULL,
    cost_price DECIMAL(10, 2) NOT NULL,
    profit_margin DECIMAL(5, 2),
    strategy_used VARCHAR(100) DEFAULT 'AI',
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_history_product (product_id)
);

-- 3. Competitor Prices Table
CREATE TABLE IF NOT EXISTS competitor_prices (
    competitor_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    competitor_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK(price >= 0),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_competitor_product (product_id)
);

-- 4. Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
);

-- 5. Pricing Rules Table
CREATE TABLE IF NOT EXISTS pricing_rules (
    rule_id INT PRIMARY KEY AUTO_INCREMENT,
    rule_name VARCHAR(100) NOT NULL,
    min_margin DECIMAL(5, 2),
    max_margin DECIMAL(5, 2),
    demand_weight DECIMAL(3, 2) DEFAULT 0.5,
    competitor_weight DECIMAL(3, 2) DEFAULT 0.3,
    cost_weight DECIMAL(3, 2) DEFAULT 0.2,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- SAMPLE DATA
-- ============================================

-- Sample Products
INSERT INTO products (product_name, category, cost_price, competitor_price) VALUES
('Laptop', 'Electronics', 500.00, 575.00),
('Smartphone', 'Electronics', 200.00, 230.00),
('Headphones', 'Audio', 30.00, 40.00),
('Tablet', 'Electronics', 150.00, 170.00),
('Monitor', 'Peripherals', 200.00, 240.00);

-- Sample Pricing Rules
INSERT INTO pricing_rules (rule_name, min_margin, max_margin, demand_weight, competitor_weight, cost_weight) VALUES
('Standard', 15.00, 40.00, 0.5, 0.3, 0.2),
('Premium', 25.00, 50.00, 0.6, 0.2, 0.2),
('Budget', 10.00, 25.00, 0.4, 0.4, 0.2);
