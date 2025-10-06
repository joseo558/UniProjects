/*
    FILE 1
    Create database and tables for AdventureWorks

    PascalCase for table names
    camelCase for column names
    Type_Snake_Case for constraints, functions, procedures, triggers, views, etc.

    @author: Jose Oliveira; Diogo Oliveira
 */

USE master;
GO

/*
-- Check if you are using docker image, abort all if not
IF NOT EXISTS (SELECT physical_name FROM sys.master_files WHERE physical_name LIKE N'/var/opt/mssql/data%')
BEGIN
    THROW 50000, N'You are not using docker image. Abort!', 1;
END
GO

-- Create directories (Warning: paths are for docker MS SQLServer 2022-latest image)
-- If not using docker image, change the paths!!

EXEC sys.xp_create_subdir N'/var/opt/mssql/data/AWprimary'; -- If exists does nothing
EXEC sys.xp_create_subdir N'/var/opt/mssql/data/AWwrite';
EXEC sys.xp_create_subdir N'/var/opt/mssql/data/AWread';
GO

-- Create DB and FileGroups
DROP DATABASE IF EXISTS AdventureWorks;
GO
CREATE DATABASE AdventureWorks
ON PRIMARY
(
    NAME = AWprimary,
    FILENAME = N'/var/opt/mssql/data/AWprimary/AWprimary.mdf',
    SIZE = 10MB,
    MAXSIZE = 50MB,
    FILEGROWTH = 5MB
),
FILEGROUP FileGroup_Write
(
    NAME = AWwrite,
    FILENAME = N'/var/opt/mssql/data/AWwrite/AWwrite.ndf',
    SIZE = 20MB,
    MAXSIZE = 100MB,
    FILEGROWTH = 10MB
),
FILEGROUP FileGroup_Read
(
    NAME = AWread,
    FILENAME = N'/var/opt/mssql/data/AWread/AWread.ndf',
    SIZE = 10MB,
    MAXSIZE = 50MB,
    FILEGROWTH = 5MB
)
LOG ON
(
    NAME = AWlog,
    FILENAME = N'/var/opt/mssql/data/AWprimary/AWlog.ldf',
    SIZE = 10MB,
    MAXSIZE = 50MB,
    FILEGROWTH = 5MB
)
COLLATE Latin1_General_100_CI_AS_SC_UTF8;
GO
 */

-- Create directories (Windows)
EXEC sys.xp_create_subdir N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\AWprimary';-- If exists does nothing
EXEC sys.xp_create_subdir N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\AWwrite';
EXEC sys.xp_create_subdir N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\AWread';
GO

-- Create DB and FileGroups
DROP DATABASE IF EXISTS AdventureWorks;
GO
CREATE DATABASE AdventureWorks
ON PRIMARY
(
    NAME = AWprimary,
    FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\AWprimary.mdf',
    SIZE = 10MB,
    MAXSIZE = 50MB,
    FILEGROWTH = 5MB
),
FILEGROUP FileGroup_Write
(
    NAME = AWwrite,
    FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\AWwrite.ndf',
    SIZE = 20MB,
    MAXSIZE = 100MB,
    FILEGROWTH = 10MB
),
FILEGROUP FileGroup_Read
(
    NAME = AWread,
    FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\AWread.ndf',
    SIZE = 10MB,
    MAXSIZE = 50MB,
    FILEGROWTH = 5MB
)
LOG ON
(
    NAME = AWlog,
    FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\AWlog.ldf',
    SIZE = 10MB,
    MAXSIZE = 50MB,
    FILEGROWTH = 5MB
)
COLLATE Latin1_General_100_CI_AS_SC_UTF8;
GO
ALTER DATABASE AdventureWorks SET RECOVERY FULL;
GO
-- UTF-8 support, VARCHAR is UTF-8 by default (SQL Server 2019+), NVARCHAR is UTF-16

-- NVARCHAR only if necessary to store characters that are not in the UTF-8 optimal range (mainly chinese, japanese, etc.)
-- VARCHAR is more efficient than NVARCHAR, uses less storage space in this use case, 1 byte vs 2 bytes (up to 4 vs 8)
-- Consume less storage space, best for occidental based internationalization (mainly latin but some chinese clients names)
-- String literals don't need N prefix, it always uses the collation of the database (which is UTF-8)
-- In this modern implementation and use case, we are ignoring all those "N" prefixes everywhere...
-- Web uses UTF-8, so it's optimized for a Web App

-- Create schemas
USE AdventureWorks;
GO
CREATE SCHEMA Localization;
GO
CREATE SCHEMA Accounts;
GO
CREATE SCHEMA Products;
GO
CREATE SCHEMA Customers;
GO
CREATE SCHEMA Sales;
GO
CREATE SCHEMA Monitoring; -- logs and statistics
GO
-- Administrator = all access
-- SalesPerson = write on Sales, Customers, Localization, read Products (not all tables!)
-- SalesTerritory = only read in its view

/*
    Localization and Postal Code
 */

-- Continent
DROP TABLE IF EXISTS Localization.Continent; -- salesTerritory excel
GO
CREATE TABLE Localization.Continent
(
    continentID INT PRIMARY KEY IDENTITY(1,1),
    continentName VARCHAR(20) NOT NULL UNIQUE
) ON FileGroup_Read;
GO

-- Country
DROP TABLE IF EXISTS Localization.Country; -- customer excel
GO
CREATE TABLE Localization.Country
(
    countryID INT PRIMARY KEY IDENTITY(1,1),
    countryCode CHAR(2) NOT NULL UNIQUE,
    -- ISO alpha-2 code
    countryName VARCHAR(50) NOT NULL UNIQUE,
    continentID INT NOT NULL,
    CONSTRAINT FK_Country_Continent FOREIGN KEY (continentID) REFERENCES Localization.Continent(continentID)
) ON FileGroup_Read;
GO

-- State
DROP TABLE IF EXISTS Localization.State; -- customer excel
GO
CREATE TABLE Localization.State
(
    stateID INT PRIMARY KEY IDENTITY(1,1),
    stateCode VARCHAR(5) NOT NULL,
    stateName VARCHAR(50) NOT NULL,
    countryID INT NOT NULL,
    CONSTRAINT FK_State_Country FOREIGN KEY (countryID) REFERENCES Localization.Country(countryID),
    CONSTRAINT Unique_State_Country UNIQUE (stateName, countryID),
    CONSTRAINT Unique_StateCode_Country UNIQUE (stateCode, countryID)
) ON FileGroup_Read;
GO

-- City
DROP TABLE IF EXISTS Localization.City; -- customer excel
GO
CREATE TABLE Localization.City
(
    cityID INT PRIMARY KEY IDENTITY(1,1),
    cityName VARCHAR(50) NOT NULL,
    stateID INT NOT NULL,
    CONSTRAINT FK_City_State FOREIGN KEY (stateID) REFERENCES Localization.State(stateID),
    CONSTRAINT Unique_City_State UNIQUE (cityName, stateID)-- city name can be repeated in different states (Saint-Ouen in france)
) ON FileGroup_Read;

-- PostalCode
DROP TABLE IF EXISTS Localization.PostalCode; -- customer excel
GO
CREATE TABLE Localization.PostalCode
(
    postalCodeID INT PRIMARY KEY IDENTITY(1,1),
    postalCode VARCHAR(20) NOT NULL,
    cityID INT NOT NULL,
    CONSTRAINT FK_PostalCode_City FOREIGN KEY (cityID) REFERENCES Localization.City(cityID),
    CONSTRAINT Unique_PostalCode_City UNIQUE (postalCode, cityID)
) ON FileGroup_Read;
GO

-- Sales Territory
DROP TABLE IF EXISTS Localization.SalesTerritory; -- salesTerritory excel
GO
CREATE TABLE Localization.SalesTerritory
(
    salesTerritoryID INT PRIMARY KEY IDENTITY(1,1),
    salesTerritoryRegion VARCHAR(50) NOT NULL,
    countryID INT NOT NULL,
    CONSTRAINT FK_SalesTerritory_Country FOREIGN KEY (countryID) REFERENCES Localization.Country(countryID),
    CONSTRAINT Unique_SalesTerritoryRegion_Country UNIQUE (salesTerritoryRegion, countryID)
) ON FileGroup_Read;
GO

/*
    Product
 */

-- ProductCategory
DROP TABLE IF EXISTS Products.ProductCategory; -- product excel
GO
CREATE TABLE Products.ProductCategory
(
    productCategoryID INT PRIMARY KEY IDENTITY(1,1),
    productCategoryName VARCHAR(50) NOT NULL UNIQUE
) ON FileGroup_Read;
GO
-- ProductSubcategory
DROP TABLE IF EXISTS Products.ProductSubcategory; -- productsubcategory excel
GO
CREATE TABLE Products.ProductSubcategory
(
    productSubcategoryID INT PRIMARY KEY IDENTITY(1,1),
    productSubcategoryName VARCHAR(50) NOT NULL,
    productCategoryID INT NOT NULL,
    CONSTRAINT FK_ProductSubcategory_Category FOREIGN KEY (productCategoryID) REFERENCES Products.ProductCategory(productCategoryID),
    CONSTRAINT Unique_ProductSubcategory_Category UNIQUE (productSubcategoryName, productCategoryID)
) ON FileGroup_Read;
GO

-- Color
DROP TABLE IF EXISTS Products.Color;-- product excel
CREATE TABLE Products.Color
(
    colorID INT PRIMARY KEY IDENTITY(1,1),
    colorName VARCHAR(50) NOT NULL UNIQUE
) ON FileGroup_Read;
GO

-- ModelStyle (Unisex, Woman, Men)
DROP TABLE IF EXISTS Products.ModelStyle;-- product excel
CREATE TABLE Products.ModelStyle
(
    modelStyleID INT PRIMARY KEY IDENTITY(1,1),
    modelStyleCode CHAR(1) NOT NULL UNIQUE,
    modelStyleName VARCHAR(50) NOT NULL UNIQUE
) ON FileGroup_Read;
GO

-- ModelClass (High, Medium, Low)
DROP TABLE IF EXISTS Products.ModelClass;-- product excel
CREATE TABLE Products.ModelClass
(
    modelClassID INT PRIMARY KEY IDENTITY(1,1),
    modelClassCode CHAR(1) NOT NULL UNIQUE,
    modelClassName VARCHAR(50) NOT NULL UNIQUE
) ON FileGroup_Read;
GO

-- ProductLine (Mountain, Road, Touring, Sport)
DROP TABLE IF EXISTS Products.ProductLine;-- product excel
CREATE TABLE Products.ProductLine
(
    productLineID INT PRIMARY KEY IDENTITY(1,1),
    productLineCode CHAR(1) NOT NULL UNIQUE,
    productLineName VARCHAR(50) NOT NULL UNIQUE
) ON FileGroup_Read;
GO

-- Unit for size (cm)
DROP TABLE IF EXISTS Products.SizeUnit; -- product excel
GO
CREATE TABLE Products.SizeUnit
(
    sizeUnitID INT PRIMARY KEY IDENTITY(1,1),
    sizeUnitCode CHAR(2) NOT NULL UNIQUE,
    sizeUnitName VARCHAR(50) NOT NULL
) ON FileGroup_Read;
GO

-- Size range
DROP TABLE IF EXISTS Products.SizeRange; -- product excel
GO
CREATE TABLE Products.SizeRange
(
    sizeRangeID INT PRIMARY KEY IDENTITY(1,1),
    sizeRangeMin TINYINT NOT NULL,
    -- 0 to 255
    sizeRangeMax TINYINT NOT NULL,
    sizeUnitID INT NOT NULL,
    CONSTRAINT FK_SizeRange_SizeUnit FOREIGN KEY (sizeUnitID) REFERENCES Products.SizeUnit(sizeUnitID),
    CONSTRAINT Unique_SizeRange_Unit UNIQUE (sizeRangeMin, sizeRangeMax, sizeUnitID)
) ON FileGroup_Read;
GO

-- Unit for weight
DROP TABLE IF EXISTS Products.WeightUnit; -- product excel
GO
CREATE TABLE Products.WeightUnit
(
    weightUnitID INT PRIMARY KEY IDENTITY(1,1),
    weightUnitCode CHAR(2) NOT NULL UNIQUE,
    weightUnitName VARCHAR(50) NOT NULL
) ON FileGroup_Read;
GO

-- Model
DROP TABLE IF EXISTS Products.Model; -- product excel
GO
CREATE TABLE Products.Model
(
    modelID INT PRIMARY KEY IDENTITY(1,1),
    modelName VARCHAR(50) NOT NULL UNIQUE,
    modelDescription VARCHAR(255) NOT NULL,
    productSubcategoryID INT NOT NULL,
    modelStyleID INT,-- can be null
    modelClassID INT,-- can be null
    productLineID INT,-- can be null
    sizeUnitID INT,-- can be null
    weightUnitID INT,-- can be null
    CONSTRAINT FK_Model_ProductSubcategory FOREIGN KEY (productSubcategoryID) REFERENCES Products.ProductSubcategory(productSubcategoryID),
    CONSTRAINT FK_Model_ModelStyle FOREIGN KEY (modelStyleID) REFERENCES Products.ModelStyle(modelStyleID),
    CONSTRAINT FK_Model_ModelClass FOREIGN KEY (modelClassID) REFERENCES Products.ModelClass(modelClassID),
    CONSTRAINT FK_Model_ProductLine FOREIGN KEY (productLineID) REFERENCES Products.ProductLine(productLineID),
    CONSTRAINT FK_Model_SizeUnit FOREIGN KEY (sizeUnitID) REFERENCES Products.SizeUnit(sizeUnitID),
    CONSTRAINT FK_Model_WeightUnit FOREIGN KEY (weightUnitID) REFERENCES Products.WeightUnit(weightUnitID),
) ON FileGroup_Read;
GO

-- Product
DROP TABLE IF EXISTS Products.Product; -- product excel
GO
CREATE TABLE Products.Product
(
    productID INT PRIMARY KEY IDENTITY (1,1),
    productName VARCHAR(50),-- if null show in view concat(model name, color, size)
    modelID INT NOT NULL,
    colorID INT,-- can be null
    productSafetyStockLevel INT NOT NULL,
    productStandardCost DECIMAL(16, 4),
    productListPrice DECIMAL(16, 4),
    productDealerPrice DECIMAL(16, 4),
    productSize VARCHAR(5),-- can be null
    productSizeRangeID INT,-- can be null
    productWeight DECIMAL(16, 4),-- can be null
    productDaysToManufacture SMALLINT NOT NULL,-- 0 to 32767
    CONSTRAINT FK_Product_Model FOREIGN KEY (modelID) REFERENCES Products.Model(modelID),
    CONSTRAINT FK_Product_Color FOREIGN KEY (colorID) REFERENCES Products.Color(colorID),
    CONSTRAINT FK_Product_SizeRange FOREIGN KEY (productSizeRangeID) REFERENCES Products.SizeRange(sizeRangeID)
) ON FileGroup_Read;
GO


/*
    Customer
 */

-- PersonTitle
DROP TABLE IF EXISTS Customers.PersonTitle; -- customer excel
GO
CREATE TABLE Customers.PersonTitle
(
    personTitleID INT PRIMARY KEY IDENTITY(1,1),
    personTitleName VARCHAR(20) NOT NULL UNIQUE
) ON FileGroup_Read;
GO

-- PersonGender
DROP TABLE IF EXISTS Customers.PersonGender; -- customer excel
GO
CREATE TABLE Customers.PersonGender
(
    personGenderID INT PRIMARY KEY IDENTITY(1,1),
    personGenderCode CHAR(1) NOT NULL UNIQUE,
    personGenderName VARCHAR(20) NOT NULL UNIQUE
) ON FileGroup_Read;
GO

-- PersonMaritalStatus
DROP TABLE IF EXISTS Customers.PersonMaritalStatus; -- customer excel
GO
CREATE TABLE Customers.PersonMaritalStatus
(
    personMaritalStatusID INT PRIMARY KEY IDENTITY(1,1),
    personMaritalStatusCode CHAR(1) NOT NULL UNIQUE,
    personMaritalStatusName VARCHAR(20) NOT NULL UNIQUE
) ON FileGroup_Read;
GO

-- PersonYearlyIncome
DROP TABLE IF EXISTS Customers.PersonYearlyIncome; -- customer excel
GO
CREATE TABLE Customers.PersonYearlyIncome
(
    personYearlyIncomeID INT PRIMARY KEY IDENTITY(1,1),
    personYearlyIncomeRange INT NOT NULL UNIQUE,
    CONSTRAINT Check_PersonYearlyIncomeRange CHECK (personYearlyIncomeRange >= 0)
) ON FileGroup_Read;
GO

-- PersonEducation
DROP TABLE IF EXISTS Customers.PersonEducation; -- customer excel
GO
CREATE TABLE Customers.PersonEducation
(
    personEducationID INT PRIMARY KEY IDENTITY(1,1),
    personEducationName VARCHAR(50) NOT NULL UNIQUE
) ON FileGroup_Read;
GO

-- PersonOccupation
DROP TABLE IF EXISTS Customers.PersonOccupation; -- customer excel
GO
CREATE TABLE Customers.PersonOccupation
(
    personOccupationID INT PRIMARY KEY IDENTITY(1,1),
    personOccupationName VARCHAR(50) NOT NULL UNIQUE
) ON FileGroup_Read;
GO

-- Customer
DROP TABLE IF EXISTS Customers.Customer; -- customer excel
GO
CREATE TABLE Customers.Customer
(
    customerID INT PRIMARY KEY IDENTITY(1,1),
    personTitleID INT,-- can be null
    customerFirstName VARCHAR(50) NOT NULL,
    customerMiddleName VARCHAR(50),-- can be null
    customerLastName VARCHAR(50) NOT NULL,
    customerBirthDate DATE NOT NULL,
    personMaritalStatusID INT NOT NULL,
    personGenderID INT NOT NULL,
    customerEmailAddress VARCHAR(50) NOT NULL UNIQUE,
    personYearlyIncomeID INT NOT NULL,
    personEducationID INT NOT NULL,
    personOccupationID INT NOT NULL,
    customerNumberCarsOwned TINYINT NOT NULL,-- 0 to 255
    customerAddressLine VARCHAR(50) NOT NULL,
    customerPostalCodeID INT NOT NULL,
    customerSalesTerritoryID INT NOT NULL,
    customerPhone VARCHAR(20) NOT NULL,-- has duplicates
    customerDateFirstPurchase DATE NOT NULL,
    CONSTRAINT FK_Customer_PersonTitle FOREIGN KEY (personTitleID) REFERENCES Customers.PersonTitle(personTitleID),
    CONSTRAINT FK_Customer_PersonMaritalStatus FOREIGN KEY (personMaritalStatusID) REFERENCES Customers.PersonMaritalStatus(personMaritalStatusID),
    CONSTRAINT FK_Customer_PersonGender FOREIGN KEY (personGenderID) REFERENCES Customers.PersonGender(personGenderID),
    CONSTRAINT FK_Customer_PersonYearlyIncome FOREIGN KEY (personYearlyIncomeID) REFERENCES Customers.PersonYearlyIncome(personYearlyIncomeID),
    CONSTRAINT FK_Customer_PersonEducation FOREIGN KEY (personEducationID) REFERENCES Customers.PersonEducation(personEducationID),
    CONSTRAINT FK_Customer_PersonOccupation FOREIGN KEY (personOccupationID) REFERENCES Customers.PersonOccupation(personOccupationID),
    CONSTRAINT FK_Customer_PostalCode FOREIGN KEY (customerPostalCodeID) REFERENCES Localization.PostalCode(postalCodeID),
    CONSTRAINT FK_Customer_SalesTerritory FOREIGN KEY (customerSalesTerritoryID) REFERENCES Localization.SalesTerritory(salesTerritoryID)
) ON FileGroup_Write;
GO

/*
    Order/Sales
 */

-- Currency
DROP TABLE IF EXISTS Sales.Currency; -- currency excel
GO
CREATE TABLE Sales.Currency
(
    currencyID INT PRIMARY KEY IDENTITY(1,1),
    currencyCode CHAR(3) NOT NULL UNIQUE,
    currencyName VARCHAR(50) NOT NULL UNIQUE
) ON FileGroup_Read;
GO

-- SalesOrder
DROP TABLE IF EXISTS Sales.SalesOrder; -- sales excel
GO
CREATE TABLE Sales.SalesOrder
(
    salesOrderID INT PRIMARY KEY IDENTITY(1,1),
    salesOrderNumber VARCHAR(20) NOT NULL UNIQUE,
    customerID INT NOT NULL,
    currencyID INT NOT NULL,
    salesTerritoryID INT NOT NULL,
    salesOrderDate DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    salesOrderShipDate DATETIME2 NOT NULL,
    salesOrderDueDate DATETIME2 NOT NULL,
    CONSTRAINT FK_SalesOrder_Customer FOREIGN KEY (customerID) REFERENCES Customers.Customer(customerID),
    CONSTRAINT FK_SalesOrder_Currency FOREIGN KEY (currencyID) REFERENCES Sales.Currency(currencyID),
    CONSTRAINT FK_SalesOrder_SalesTerritory FOREIGN KEY (salesTerritoryID) REFERENCES Localization.SalesTerritory(salesTerritoryID),
    CONSTRAINT Check_SalesOrderShipDate CHECK (salesOrderShipDate >= salesOrderDate),
    CONSTRAINT Check_SalesOrderDueDate CHECK (salesOrderDueDate >= salesOrderShipDate)
) ON FileGroup_Write;
GO

-- SalesOrderLine
DROP TABLE IF EXISTS Sales.SalesOrderLine; -- sales excel
GO
CREATE TABLE Sales.SalesOrderLine
(
    salesOrderLineNumber INT NOT NULL,
    salesOrderID INT NOT NULL,
    productID INT NOT NULL,
    salesOrderLineQuantity INT NOT NULL,
    salesOrderLineTaxAmt DECIMAL(16, 2) NOT NULL,
    salesOrderLineFreight DECIMAL(16, 2) NOT NULL,
    salesOrderLineUnitPrice DECIMAL(16, 2) NOT NULL,
    PRIMARY KEY (salesOrderID, salesOrderLineNumber),
    CONSTRAINT FK_SalesOrderLine_SalesOrder FOREIGN KEY (salesOrderID) REFERENCES Sales.SalesOrder(salesOrderID),
    CONSTRAINT FK_SalesOrderLine_Product FOREIGN KEY (productID) REFERENCES Products.Product(productID),
    CONSTRAINT Check_SalesOrderLineQuantity CHECK (salesOrderLineQuantity > 0),
    CONSTRAINT Check_SalesOrderLineTaxAmt CHECK (salesOrderLineTaxAmt >= 0),
    CONSTRAINT Check_SalesOrderLineFreight CHECK (salesOrderLineFreight >= 0)
) ON FileGroup_Write;
GO


/*
    User
 */

-- UserRole
DROP TABLE IF EXISTS Accounts.UserRole;
GO
CREATE TABLE Accounts.UserRole
(
    userRoleID INT PRIMARY KEY IDENTITY(1,1),
    userRoleName VARCHAR(20) NOT NULL UNIQUE,
    userRoleDBAccess VARCHAR(20) NOT NULL
) ON FileGroup_Read;
GO

-- UserAccount
DROP TABLE IF EXISTS Accounts.UserAccount;
GO
CREATE TABLE Accounts.UserAccount
(
    userAccountID INT PRIMARY KEY IDENTITY(1,1),
    customerID INT NOT NULL,
    userAccountPasswordHash VARBINARY(64) NOT NULL,-- Can store SHA-256 (32) and SHA-512 (64) hash
    -- https://learn.microsoft.com/en-us/sql/t-sql/functions/hashbytes-transact-sql?view=sql-server-ver16
    userRoleID INT NOT NULL,
    userAccountQuestion VARCHAR(50) NOT NULL,
    userAccountAnswer VARBINARY(64) NOT NULL,-- Can store SHA-256 (32) and SHA-512 (64) hash
    CONSTRAINT FK_UserAccount_Customer FOREIGN KEY (customerID) REFERENCES Customers.Customer(customerID),
    CONSTRAINT FK_UserAccount_UserRole FOREIGN KEY (userRoleID) REFERENCES Accounts.UserRole(userRoleID)
) ON FileGroup_Read;
GO

/*
    Monitoring: logs and statistics
 */

-- LogUser
DROP TABLE IF EXISTS Monitoring.LogUser;
GO
CREATE TABLE Monitoring.LogUser
(
    logUserID INT PRIMARY KEY IDENTITY(1,1),
    userAccountID INT NOT NULL,
    logUserAction VARCHAR(50) NOT NULL,
    logUserDate DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT FK_LogUser_UserAccount FOREIGN KEY (userAccountID) REFERENCES Accounts.UserAccount(userAccountID)
) ON [PRIMARY];
GO

-- LogSentEmail
DROP TABLE IF EXISTS Monitoring.LogSentEmail;
GO
CREATE TABLE Monitoring.LogSentEmail
(
    logSentEmailID INT PRIMARY KEY IDENTITY(1,1),
    userAccountID INT NOT NULL,
    logSentEmailMessage VARCHAR(250) NOT NULL,
    logSentEmailDate DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT FK_LogSentEmail_UserAccount FOREIGN KEY (userAccountID) REFERENCES Accounts.UserAccount(userAccountID)
) ON [PRIMARY];
GO

-- LogError
DROP TABLE IF EXISTS Monitoring.LogError;
GO
CREATE TABLE Monitoring.LogError
(
    logErrorID INT PRIMARY KEY IDENTITY(1,1),
    userAccountID INT NOT NULL,
    logErrorTable VARCHAR(50) NOT NULL,
    logErrorOperation VARCHAR(50) NOT NULL,
    logErrorMessage VARCHAR(250) NOT NULL,
    logErrorDate DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT FK_LogError_UserAccount FOREIGN KEY (userAccountID) REFERENCES Accounts.UserAccount(userAccountID)
) ON [PRIMARY];
GO

-- DBStatistics
DROP TABLE IF EXISTS Monitoring.DBStatistics;
GO
CREATE TABLE Monitoring.DBStatistics
(
    dbStatisticsID INT PRIMARY KEY IDENTITY(1,1),
    dbStatisticsTable VARCHAR(50) NOT NULL,
    dbStatisticsNumberRows BIGINT NOT NULL,
    dbStatisticsSpaceKB BIGINT NOT NULL, -- space used. sys tables store as bigint
    dbStatisticsSpaceReservedKB BIGINT NOT NULL, -- space reserved. Free space is space reserved - space used
    dbStatisticsDate DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT Check_DBStatisticsNumberRows CHECK (dbStatisticsNumberRows >= 0),
    CONSTRAINT Check_DBStatisticsSpaceKB CHECK (dbStatisticsSpaceKB >= 0),
    CONSTRAINT Check_DBStatisticsSpaceReservedKB CHECK (dbStatisticsSpaceReservedKB >= 0)
) ON [PRIMARY];
GO

-- Consultas para o MongoDB

SELECT * FROM Sales.SalesOrder;
SELECT * FROM Sales.SalesOrderLine;
SELECT * FROM Localization.SalesTerritory;
SELECT * FROM Localization.State;
SELECT * FROM Localization.City;
SELECT * FROM Products.Product;
SELECT * FROM Products.Model;