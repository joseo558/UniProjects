/*
    FILE 4
    Test migration script for AdventureWorks database

    PascalCase for table names
    camelCase for column names
    Type_Snake_Case for constraints, functions, procedures, triggers, views, etc.

    @author: Jose Oliveira; Diogo Oliveira
 */
USE AdventureWorks;
GO

-- Count Products
SELECT COUNT(*) FROM AdventureWorksLegacy.dbo.Products$;
select count(*) from AdventureWorks.Products.product;

--------------------------------------------------------------------------------------------------------------------------

-- Count Sales 
SELECT COUNT( DISTINCT CAST(SalesOrderNumber AS VARCHAR(20))) FROM AdventureWorksLegacy.dbo.Sales$;
select count(*) from AdventureWorks.Sales.SalesOrder;

--------------------------------------------------------------------------------------------------------------------------

-- Total sales by Customer
SELECT
    C.customerID, -- Select the customer ID from the Customers table
    C.customerFirstName, -- Select the customer's first name from the Customers table
    C.customerLastName, -- Select the customer's last name from the Customers table
    SUM(SL.salesOrderLineQuantity * SL.salesOrderLineUnitPrice) AS totalSalesValue  -- Calculate the total sales value for each customer by multiplying quantity and unit price
FROM
    Sales.SalesOrder S -- The Sales.SalesOrder table contains information about sales orders
JOIN
    Customers.Customer C ON S.customerID = C.customerID
JOIN
    Sales.SalesOrderLine SL ON SL.salesOrderID = S.salesOrderID
GROUP BY -- Group the results by customer to calculate total sales per customer
    C.customerID, C.customerFirstName, C.customerLastName
ORDER BY -- Order the results by total sales value in descending order to show the highest sales first
    totalSalesValue DESC;

--------------------------------------------------------------------------------------------------------------------------

-- Total monetary sales per year

SELECT
    YEAR(S.salesOrderDate) AS salesYear, -- Extract the year from the sales order date and alias it as salesYear
    SUM(SL.salesOrderLineQuantity * SL.salesOrderLineUnitPrice) AS totalSalesValue -- Calculate the total sales value by multiplying the quantity by the unit price
FROM
    Sales.SalesOrder S -- The Sales.SalesOrder table contains information about the sales orders
JOIN
    Sales.SalesOrderLine SL ON SL.salesOrderID = S.salesOrderID
GROUP BY -- Group the results by year to calculate total sales per year
    YEAR(S.salesOrderDate)
ORDER BY -- Order the results by year in descending order
    salesYear DESC;

--------------------------------------------------------------------------------------------------------------------------

-- Total monetary sales per year and per Product

SELECT
    YEAR(S.salesOrderDate) AS salesYear, -- Extract the year from the sales order date and alias it as salesYear
    P.productID, -- Select the product ID from the Products table
    P.productName, -- Select the product name from the Products table
    SUM(SL.salesOrderLineQuantity * SL.salesOrderLineUnitPrice) AS totalSalesValue -- Calculate the total sales value by multiplying the quantity by the unit price
FROM
    Sales.SalesOrder S -- The Sales.SalesOrder table contains information about the sales orders
JOIN
    Sales.SalesOrderLine SL ON SL.salesOrderID = S.salesOrderID
JOIN
    Products.Product P ON SL.productID = P.productID
GROUP BY -- Group the results by year, product ID, and product name to calculate sales for each combination
    YEAR(S.salesOrderDate), P.productID, P.productName 
ORDER BY -- Order the results first by year in descending order, and then by total sales value in descending order
    salesYear DESC, totalSalesValue DESC;