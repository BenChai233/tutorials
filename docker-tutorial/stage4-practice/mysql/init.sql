-- Docker 网络实践 - MySQL 初始化脚本

CREATE DATABASE IF NOT EXISTS testdb;
USE testdb;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建网络测试表
CREATE TABLE IF NOT EXISTS network_tests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    test_name VARCHAR(100) NOT NULL,
    source_service VARCHAR(50) NOT NULL,
    target_service VARCHAR(50) NOT NULL,
    test_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN DEFAULT FALSE,
    response_time_ms INT,
    notes TEXT
);

-- 插入示例数据
INSERT INTO users (username, email) VALUES
    ('alice', 'alice@example.com'),
    ('bob', 'bob@example.com'),
    ('charlie', 'charlie@example.com');

-- 插入网络测试记录
INSERT INTO network_tests (test_name, source_service, target_service, success, response_time_ms, notes) VALUES
    ('ping_test', 'webapp', 'mysql', TRUE, 2, 'Initial connectivity test'),
    ('dns_test', 'webapp', 'api-service', TRUE, 1, 'DNS resolution test');

