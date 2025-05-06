-- 1. List all customers with their assigned sales representative's name and job title
SELECT 
  c.customerName,
  CONCAT(e.firstName, ' ', e.lastName) AS salesRep,
  e.jobTitle
FROM customers c
JOIN employees e ON c.salesRepEmployeeNumber = e.employeeNumber;

-- 2. Find products that have never been ordered
SELECT 
  p.productCode, p.productName
FROM products p
LEFT JOIN orderdetails od ON p.productCode = od.productCode
WHERE od.productCode IS NULL;


-- 3. List all orders with customer name, order status, and total order value
SELECT 
  o.orderNumber,
  c.customerName,
  o.status,
  SUM(od.quantityOrdered * od.priceEach) AS totalOrderValue
FROM orders o
JOIN customers c ON o.customerNumber = c.customerNumber
JOIN orderdetails od ON o.orderNumber = od.orderNumber
GROUP BY o.orderNumber, c.customerName, o.status
ORDER BY totalOrderValue DESC;

-- 4. Show each employee with their manager’s name
SELECT 
  e.employeeNumber,
  CONCAT(e.firstName, ' ', e.lastName) AS employeeName,
  CONCAT(m.firstName, ' ', m.lastName) AS managerName
FROM employees e
LEFT JOIN employees m ON e.reportsTo = m.employeeNumber;

-- 5. Find the top 5 customers by total payments made
SELECT 
  c.customerName,
  SUM(p.amount) AS totalPaid
FROM payments p
JOIN customers c ON p.customerNumber = c.customerNumber
GROUP BY c.customerNumber, c.customerName
ORDER BY totalPaid DESC
LIMIT 5;

-- 6. Count number of orders and total amount per product
SELECT 
  p.productName,
  COUNT(od.orderNumber) AS totalOrders,
  SUM(od.quantityOrdered * od.priceEach) AS totalRevenue
FROM products p
JOIN orderdetails od ON p.productCode = od.productCode
GROUP BY p.productCode, p.productName
ORDER BY totalRevenue DESC;

-- 7. List offices and number of employees in each office
SELECT 
  o.city,
  COUNT(e.employeeNumber) AS numEmployees
FROM offices o
LEFT JOIN employees e ON o.officeCode = e.officeCode
GROUP BY o.officeCode, o.city
ORDER BY numEmployees DESC;

-- 8. Get average order value by country
SELECT 
  c.country,
  AVG(od.quantityOrdered * od.priceEach) AS avgOrderValue
FROM customers c
JOIN orders o ON c.customerNumber = o.customerNumber
JOIN orderdetails od ON o.orderNumber = od.orderNumber
GROUP BY c.country
ORDER BY avgOrderValue DESC;

-- 9. Get all orders placed in the last 30 days (assuming today is '2025-05-06')
SELECT 
  o.orderNumber,
  o.orderDate,
  c.customerName
FROM orders o
JOIN customers c ON o.customerNumber = c.customerNumber
WHERE o.orderDate >= DATE_SUB('2025-05-06', INTERVAL 30 DAY)
ORDER BY o.orderDate DESC;

-- 10. Show all products with a CASE classification based on stock quantity
SELECT 
  productName,
  quantityInStock,
  CASE
    WHEN quantityInStock > 5000 THEN 'High Stock'
    WHEN quantityInStock BETWEEN 1000 AND 5000 THEN 'Medium Stock'
    ELSE 'Low Stock'
  END AS stockLevel
FROM products;
