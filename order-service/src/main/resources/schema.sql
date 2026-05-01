CREATE DATABASE IF NOT EXISTS shopforge_orders;
USE shopforge_orders;

CREATE TABLE IF NOT EXISTS orders (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number       VARCHAR(32)    NOT NULL UNIQUE,
    user_email         VARCHAR(255)   NOT NULL,
    status             VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    total_amount       DECIMAL(12,2)  NOT NULL,
    shipping_street    VARCHAR(255),
    shipping_city      VARCHAR(100),
    shipping_state     VARCHAR(100),
    shipping_zip       VARCHAR(20),
    shipping_country   VARCHAR(100),
    payment_intent_id  VARCHAR(64),
    payment_charge_id  VARCHAR(64),
    created_at         DATETIME(6)    NOT NULL,
    updated_at         DATETIME(6)    NOT NULL,
    INDEX idx_user_email (user_email),
    INDEX idx_status     (status),
    INDEX idx_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS order_items (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT         NOT NULL,
    product_id   BIGINT         NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    quantity     INT            NOT NULL,
    unit_price   DECIMAL(10,2)  NOT NULL,
    subtotal     DECIMAL(12,2)  NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id)
);
