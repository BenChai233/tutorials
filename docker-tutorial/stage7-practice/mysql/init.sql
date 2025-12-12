-- 初始化数据库脚本
-- 创建用户表和订单表

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入示例数据
INSERT INTO users (username, email, name) VALUES
('alice', 'alice@example.com', 'Alice Smith'),
('bob', 'bob@example.com', 'Bob Johnson'),
('charlie', 'charlie@example.com', 'Charlie Brown')
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO orders (user_id, product_name, quantity, amount, status) VALUES
(1, 'Laptop', 1, 999.99, 'CONFIRMED'),
(1, 'Mouse', 2, 29.98, 'SHIPPED'),
(2, 'Keyboard', 1, 79.99, 'PENDING'),
(3, 'Monitor', 1, 299.99, 'DELIVERED')
ON DUPLICATE KEY UPDATE product_name=product_name;

