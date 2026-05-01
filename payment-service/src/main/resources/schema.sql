CREATE DATABASE IF NOT EXISTS shopforge_payments;
USE shopforge_payments;

CREATE TABLE IF NOT EXISTS payment_records (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id           VARCHAR(32)    NOT NULL,
    payment_intent_id  VARCHAR(64)    UNIQUE,
    charge_id          VARCHAR(64),
    payment_method_id  VARCHAR(64),
    status             VARCHAR(20)    NOT NULL,
    amount             DECIMAL(12,2)  NOT NULL,
    currency           CHAR(3)        NOT NULL DEFAULT 'usd',
    customer_email     VARCHAR(255),
    decline_code       VARCHAR(64),
    failure_message    TEXT,
    created_at         DATETIME(6)    NOT NULL,
    INDEX idx_order_id         (order_id),
    INDEX idx_payment_intent   (payment_intent_id),
    INDEX idx_status           (status)
);
