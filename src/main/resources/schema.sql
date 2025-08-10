DROP TABLE IF EXISTS manufacturer_order_shipping_info;
DROP TABLE IF EXISTS manufacturer_orders_suppliers;
DROP TABLE IF EXISTS manufacturer_orders_products;
DROP TABLE IF EXISTS manufacturer_orders;
DROP TABLE IF EXISTS manufacturer_phone_numbers;
DROP TABLE IF EXISTS manufacturer_email_addresses;
DROP TABLE IF EXISTS product_manufacturers;
DROP TABLE IF EXISTS manufacturers;
DROP TABLE IF EXISTS orders_returned;
DROP TABLE IF EXISTS customer_orders_products;
DROP TABLE IF EXISTS sales_report;
DROP TABLE IF EXISTS cart_products;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS employee_email_addresses;
DROP TABLE IF EXISTS customer_order_shipping_info;
DROP TABLE IF EXISTS customer_orders;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS users;

-- Table to store user details
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL
);

-- Table to store user roles
CREATE TABLE IF NOT EXISTS authorities (
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS categories (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_description VARCHAR(255) NOT NULL,
    category_name VARCHAR(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS customers (
    email VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY,
    username VARCHAR(50),
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    shipping_address VARCHAR(255) NOT NULL,
    billing_address VARCHAR(255) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS products (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(255) NOT NULL,
    expiry_date DATE,
    stock_quantity INT NOT NULL,
    description TEXT,
    selling_price DECIMAL(10, 2) NOT NULL,
    maximum_retail_price DECIMAL(10, 2) NOT NULL,
    category_id BIGINT,
    image_url VARCHAR(255),
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    FULLTEXT(product_name, description)
);


CREATE TABLE IF NOT EXISTS manufacturers (
    manufacturer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    manufacturer_name VARCHAR(255) NOT NULL,
    manufacturer_address VARCHAR(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS manufacturer_email_addresses (
    manufacturer_id BIGINT NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(manufacturer_id) ON DELETE CASCADE,
    PRIMARY KEY (manufacturer_id, email_address)
);


CREATE TABLE IF NOT EXISTS manufacturer_phone_numbers (
    manufacturer_id BIGINT NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(manufacturer_id) ON DELETE CASCADE,
    PRIMARY KEY (manufacturer_id, phone_number)
);


CREATE TABLE IF NOT EXISTS product_manufacturers (
    product_id BIGINT NOT NULL,
    manufacturer_id BIGINT NOT NULL,
    cost_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(manufacturer_id) ON DELETE CASCADE,
    PRIMARY KEY (product_id, manufacturer_id)
);


CREATE TABLE IF NOT EXISTS employees (
    employee_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(10) NOT NULL,
    hire_date DATE NOT NULL,
    designation VARCHAR(255) NOT NULL,
    manager_employee_id BIGINT,
    FOREIGN KEY (manager_employee_id) REFERENCES employees(employee_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS employee_email_addresses (
    employee_id BIGINT NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    PRIMARY KEY (employee_id, email_address)
);


CREATE TABLE IF NOT EXISTS customer_orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date_of_order DATE NOT NULL,
    customer_email VARCHAR(255),
    processed_by_employee_id BIGINT,
    payment_method ENUM('CASH', 'CARD', 'NET_BANKING', 'UPI') NOT NULL,
    FOREIGN KEY (customer_email) REFERENCES customers(email) ON DELETE SET NULL,
    FOREIGN KEY (processed_by_employee_id) REFERENCES employees(employee_id) ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS customer_orders_products (
    order_id BIGINT,
    product_id BIGINT,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES customer_orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    PRIMARY KEY (order_id, product_id)
);  


CREATE TABLE IF NOT EXISTS orders_returned (
    order_id BIGINT PRIMARY KEY NOT NULL,
    return_date DATE NOT NULL,
    return_reason VARCHAR(255) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES customer_orders(order_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS customer_order_shipping_info (
    shipping_info_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    shipping_date DATE,
    expected_delivery_date DATE,
    status ENUM ('SHIPPED', 'DELIVERED', 'PENDING', 'CANCELLED') NOT NULL,
    FOREIGN KEY (order_id) REFERENCES customer_orders(order_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS manufacturer_orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ordered_from BIGINT,
    date_of_order DATE NOT NULL,
    processed_by_employee_id BIGINT,
    FOREIGN KEY (ordered_from) REFERENCES manufacturers(manufacturer_id) ON DELETE SET NULL,
    FOREIGN KEY (processed_by_employee_id) REFERENCES employees(employee_id) ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS manufacturer_orders_products (
    manufacturer_order_id BIGINT NOT NULL,
    product_id BIGINT,
    quantity INT NOT NULL,
    FOREIGN KEY (manufacturer_order_id) REFERENCES manufacturer_orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    PRIMARY KEY (manufacturer_order_id, product_id)
);



CREATE TABLE IF NOT EXISTS manufacturer_order_shipping_info (
    shipping_info_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    manufacturer_order_id BIGINT NOT NULL,
    shipping_date DATE,
    expected_delivery_date DATE,
    status ENUM ('SHIPPED', 'ARRIVED', 'PENDING', 'CANCELLED') NOT NULL,
    FOREIGN KEY (manufacturer_order_id) REFERENCES manufacturer_orders(order_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sales_report (
    day INT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    total_sales DECIMAL(10, 2) NOT NULL,
    total_orders INT NOT NULL,
    top_selling_product_id BIGINT,
    FOREIGN KEY (top_selling_product_id) REFERENCES products(product_id) ON DELETE SET NULL,
    PRIMARY KEY (day, month, year)
);

CREATE TABLE IF NOT EXISTS carts (
    cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_email VARCHAR(255) NOT NULL,
    FOREIGN KEY (customer_email) REFERENCES customers(email) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cart_products (
    cart_id BIGINT NOT NULL,
    added_on DATE NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    PRIMARY KEY (cart_id, product_id)
);

-- CREATE TRIGGER update_sales_report
-- AFTER INSERT, UPDATE, DELETE ON customer_orders
-- FOR EACH ROW
-- BEGIN
--     DECLARE day INT//
--     DECLARE month INT//
--     DECLARE year INT//
--     DECLARE total_sales DECIMAL(10, 2)//
--     DECLARE total_orders INT//
--     DECLARE top_selling_product_id BIGINT//
    
--     SET day = DAY(NEW.date_of_order)//
--     SET month = MONTH(NEW.date_of_order)//
--     SET year = YEAR(NEW.date_of_order)//
    
--     SELECT SUM(products.maximum_retail_price * customer_orders_products.quantity) INTO total_sales
--     FROM customer_orders_products
--     JOIN products ON customer_orders_products.product_id = products.product_id
--     WHERE customer_orders_products.order_id = NEW.order_id//
    
--     SELECT COUNT(*) INTO total_orders
--     FROM customer_orders
--     WHERE DAY(date_of_order) = day AND MONTH(date_of_order) = month AND YEAR(date_of_order) = year//
    
--     SELECT product_id INTO top_selling_product_id
--     FROM customer_orders_products
--     WHERE order_id = NEW.order_id
--     GROUP BY product_id
--     ORDER BY SUM(quantity) DESC
--     LIMIT 1//
    
--     IF EXISTS (SELECT * FROM sales_report WHERE day = day AND month = month AND year = year) THEN
--         UPDATE sales_report
--         SET total_sales = total_sales + NEW.total_price,
--             total_orders = total_orders + 1,
--             top_selling_product_id = top_selling_product_id
--         WHERE day = day AND month = month AND year = year//
--     ELSE
--         INSERT INTO sales_report (day, month, year, total_sales, total_orders, top_selling_product_id)
--         VALUES (day, month, year, total_sales, total_orders, top_selling_product_id)//
--     END IF//
-- END;
