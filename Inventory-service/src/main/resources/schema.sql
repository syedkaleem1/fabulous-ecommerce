CREATE DATABASE IF NOT EXISTS shopforge_inventory;
USE shopforge_inventory;

CREATE TABLE IF NOT EXISTS inventory (
    product_id        BIGINT         NOT NULL PRIMARY KEY,
    product_name      VARCHAR(255)   NOT NULL,
    stock_quantity    INT            NOT NULL DEFAULT 0,
    reserved_quantity INT            NOT NULL DEFAULT 0,
    version           BIGINT         NOT NULL DEFAULT 0,
    updated_at        DATETIME(6),
    CONSTRAINT chk_stock    CHECK (stock_quantity    >= 0),
    CONSTRAINT chk_reserved CHECK (reserved_quantity >= 0),
    CONSTRAINT chk_available CHECK (stock_quantity   >= reserved_quantity)
);

-- Seed data
INSERT INTO inventory (product_id, product_name, stock_quantity, reserved_quantity, version) VALUES
(1, 'Wireless Headphones',  150, 0, 0),
(2, 'Mechanical Keyboard',   80, 0, 0),
(3, 'USB-C Hub',            200, 0, 0),
(4, 'Webcam 4K',             45, 0, 0),
(5, 'Standing Desk Mat',    100, 0, 0);
