CREATE DATABASE food_delivery_db;
USE food_delivery_db;
DROP TABLE IF EXISTS assignments;
DROP TABLE IF EXISTS customers;
CREATE TABLE customers (customer_id INT PRIMARY KEY,order_placement_time INT NOT NULL,
travel_time INT NOT NULL);
CREATE TABLE assignments (assignment_id INT AUTO_INCREMENT PRIMARY KEY,customer_id INT NOT NULL,
assignment_result VARCHAR(50) NOT NULL,FOREIGN KEY (customer_id) REFERENCES customers(customer_id));