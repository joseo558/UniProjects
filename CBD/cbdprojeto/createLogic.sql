/*
    FILE 2
    Create database logic operations: SPs, Functions, Views, Triggers, etc.

    PascalCase for table names
    camelCase for column names
    Type_Snake_Case for constraints, functions, procedures, triggers, views, etc.

    @author: Jose Oliveira; Diogo Oliveira
 */

USE AdventureWorks;
GO

/*
    Monitoring
*/
-- Log errors
DROP PROCEDURE IF EXISTS Monitoring.sp_Log_Error;
GO

CREATE PROCEDURE Monitoring.sp_Log_Error
    @userAccountID INT, -- -1 for system errors
    @logErrorTable VARCHAR(50),
    @logErrorOperation VARCHAR(50),
    @logErrorMessage VARCHAR(250)
AS
BEGIN
    -- Check for null
    IF @userAccountID IS NULL OR
       @logErrorTable IS NULL OR @logErrorTable = '' OR
       @logErrorOperation IS NULL OR @logErrorOperation = '' OR
       @logErrorMessage IS NULL OR @logErrorMessage = ''
    BEGIN
        THROW 50000, 'One or more required fields are missing.', 1;
    END

    -- Check if the user exists
    IF NOT EXISTS (SELECT 1 FROM Accounts.UserAccount WHERE userAccountID = @userAccountID) AND @userAccountID != -1
    BEGIN
        THROW 50000, 'The specified user does not exist.', 1;
    END

    -- Check if the table exists
    DECLARE @schemaName VARCHAR(50) = SUBSTRING(@logErrorTable, 0, CHARINDEX('.', @logErrorTable));
    SET @logErrorTable = SUBSTRING(@logErrorTable, CHARINDEX('.', @logErrorTable) + 1, LEN(@logErrorTable));

    IF NOT EXISTS (
        SELECT 1 FROM sys.tables AS t
        JOIN sys.schemas AS s ON t.schema_id = s.schema_id
        WHERE t.name = @logErrorTable AND s.name = @schemaName
    )
    BEGIN
        THROW 50000, 'The specified table does not exist.', 1;
    END

    -- Check if the operation is valid
    IF @logErrorOperation NOT IN ('SELECT', 'INSERT', 'UPDATE', 'DELETE', 'OTHER')
    BEGIN
        THROW 50000, 'The specified operation is not valid.', 1;
    END

    -- Insert the error log
    INSERT INTO Monitoring.logError (userAccountID, logErrorTable, logErrorOperation, logErrorMessage)
    VALUES (@userAccountID, @logErrorTable, @logErrorOperation, @logErrorMessage);
END;

/*
    Accounts
*/
-- Add User Role
DROP PROCEDURE IF EXISTS Accounts.sp_Add_User_Role;
GO
CREATE PROCEDURE Accounts.sp_Add_User_Role
    @userRoleName VARCHAR(50),
    @userRoleDBAccess VARCHAR(50)
AS
BEGIN
    -- Check if the role already exists
    IF EXISTS (SELECT 1 FROM Accounts.UserRole WHERE userRoleName = @userRoleName)
    BEGIN
        EXEC Monitoring.sp_Log_Error -1, 'Accounts.UserRole', 'INSERT', 'A user role with this name already exists.';
        THROW 50000, 'A user role with this name already exists.', 1;
    END
    -- Check if the DBAccess role exists
    IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = @userRoleDBAccess)
    BEGIN
        EXEC Monitoring.sp_Log_Error -1, 'Accounts.UserRole', 'INSERT', 'The specified database role does not exist.';
        THROW 50000, 'The specified database role does not exist.', 1;
    END
    -- Insert the new user role
    INSERT INTO Accounts.UserRole (userRoleName, userRoleDBAccess) VALUES (@userRoleName, @userRoleDBAccess);
END;
GO

-- Add User Account
DROP PROCEDURE IF EXISTS Accounts.sp_Add_User_Account;
GO

CREATE PROCEDURE Accounts.sp_Add_User_Account
    @personTitleID INT, -- can be null
    @customerFirstName VARCHAR(50),
    @customerMiddleName VARCHAR(50), -- can be null
    @customerLastName VARCHAR(50),
    @customerBirthDate DATE,
    @personMaritalStatusID INT,
    @personGenderID INT,
    @customerEmailAddress VARCHAR(50),
    @personYearlyIncomeID INT,
    @personEducationID INT,
    @personOccupationID INT,
    @customerNumberCarsOwned TINYINT, -- 0 to 255
    @customerAddressLine VARCHAR(50),
    @customerPostalCodeID INT,
    @customerSalesTerritoryID INT,
    @customerPhone VARCHAR(20), -- has duplicates
    @customerDateFirstPurchase DATE,
    @userAccountPassword VARCHAR(50),
    @userAccountQuestion VARCHAR(50),
    @userAccountAnswer VARCHAR(50),
    @userRoleID INT
AS
BEGIN
    BEGIN TRANSACTION;

    BEGIN TRY
        DECLARE @passwordHash VARBINARY(64);
        DECLARE @answerHash VARBINARY(64);
        DECLARE @logMessage NVARCHAR(200) = 'User account created. Please confirm your email by clicking here.'; -- Log message

        DECLARE @userAccountID INT;
        DECLARE @customerID INT;

        -- Check not null values
        IF @customerFirstName IS NULL OR @customerFirstName = '' OR
           @customerLastName IS NULL OR @customerLastName = '' OR
           TRY_CONVERT(DATETIME2, @customerBirthDate) IS NULL OR
           @personMaritalStatusID IS NULL OR
           @personGenderID IS NULL OR
           @customerEmailAddress IS NULL OR @customerEmailAddress = '' OR
           @personYearlyIncomeID IS NULL OR
           @personEducationID IS NULL OR
           @personOccupationID IS NULL OR
           @customerNumberCarsOwned IS NULL OR
           @customerAddressLine IS NULL OR @customerAddressLine = '' OR
           @customerPostalCodeID IS NULL OR
           @customerSalesTerritoryID IS NULL OR
           @customerPhone IS NULL OR @customerPhone = '' OR
           TRY_CONVERT(DATETIME2, @customerDateFirstPurchase) IS NULL OR
           @userAccountPassword IS NULL OR @userAccountPassword = '' OR
           @userAccountQuestion IS NULL OR @userAccountQuestion = '' OR
           @userAccountAnswer IS NULL OR @userAccountAnswer = '' OR
           @userRoleID IS NULL
        BEGIN
            THROW 50000, 'One or more required fields are missing.', 1;
        END

        -- Check dates
        IF @customerBirthDate > GETDATE() OR @customerBirthDate < DATEADD(YEAR, -100, GETDATE())
        BEGIN
            THROW 50000, 'The birth date is not valid.', 1;
        END
        IF @customerDateFirstPurchase > GETDATE() OR @customerDateFirstPurchase < @customerBirthDate OR @customerDateFirstPurchase < DATEADD(YEAR, -100, GETDATE())
        BEGIN
            THROW 50000, 'The date of first purchase is not valid.', 1;
        END

        -- Check ids
        IF @personTitleID IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Customers.PersonTitle WHERE personTitleID = @personTitleID)
        BEGIN
            THROW 50000, 'The title does not exist.', 1;
        END
        IF NOT EXISTS (SELECT 1 FROM Customers.PersonMaritalStatus WHERE personMaritalStatusID = @personMaritalStatusID)
        BEGIN
            THROW 50000, 'The marital status does not exist.', 1;
        END
        IF NOT EXISTS (SELECT 1 FROM Customers.PersonGender WHERE personGenderID = @personGenderID)
        BEGIN
            THROW 50000, 'The gender does not exist.', 1;
        END
        IF NOT EXISTS (SELECT 1 FROM Customers.PersonYearlyIncome WHERE personYearlyIncomeID = @personYearlyIncomeID)
        BEGIN
            THROW 50000, 'The yearly income does not exist.', 1;
        END
        IF NOT EXISTS (SELECT 1 FROM Customers.PersonEducation WHERE personEducationID = @personEducationID)
        BEGIN
            THROW 50000, 'The education does not exist.', 1;
        END
        IF NOT EXISTS (SELECT 1 FROM Customers.PersonOccupation WHERE personOccupationID = @personOccupationID)
        BEGIN
            THROW 50000, 'The occupation does not exist.', 1;
        END
        IF NOT EXISTS (SELECT 1 FROM Localization.PostalCode WHERE postalCodeID = @customerPostalCodeID)
        BEGIN
            THROW 50000, 'The postal code does not exist.', 1;
        END
        IF NOT EXISTS (SELECT 1 FROM Localization.SalesTerritory WHERE salesTerritoryID = @customerSalesTerritoryID)
        BEGIN
            THROW 50000, 'The sales territory does not exist.', 1;
        END
        IF NOT EXISTS (SELECT 1 FROM Accounts.UserRole WHERE userRoleID = @userRoleID)
        BEGIN
            THROW 50000, 'The user role does not exist.', 1;
        END

        -- Check if the email is valid
        IF @customerEmailAddress NOT LIKE '%_@_%._%'
        BEGIN
            THROW 50000, 'The specified email is not valid.', 1; -- Raise an error
        END
        IF EXISTS (SELECT 1 FROM Customers.Customer WHERE customerEmailAddress = @customerEmailAddress)
        BEGIN
            THROW 50000, 'The email is already in use.', 1;
        END

        -- Check if the password is valid
        IF LEN(@userAccountPassword) < 10 OR
           @userAccountPassword NOT LIKE '%[0-9]%' OR
           @userAccountPassword NOT LIKE '%[A-Z]%' OR
           @userAccountPassword NOT LIKE '%[a-z]%'
        BEGIN
            THROW 50000, 'The password must be longer than 10 characters and include at least a number and a uppercase letter.', 1; -- Raise an error
        END

        -- Generate the hashes for the password and the security response
        SET @passwordHash = HASHBYTES('SHA2_256', @userAccountPassword);
        SET @answerHash = HASHBYTES('SHA2_256', @userAccountAnswer);

        -- Insert the new customer in the Customers.Customer table and capture the generated ID
        INSERT INTO Customers.Customer (personTitleID, customerFirstName, customerMiddleName, customerLastName, customerBirthDate, personMaritalStatusID, personGenderID, customerEmailAddress, personYearlyIncomeID, personEducationID, personOccupationID, customerNumberCarsOwned, customerAddressLine, customerPostalCodeID, customerSalesTerritoryID, customerPhone, customerDateFirstPurchase)
        VALUES
        (@personTitleID, @customerFirstName, @customerMiddleName, @customerLastName, @customerBirthDate, @personMaritalStatusID, @personGenderID, @customerEmailAddress, @personYearlyIncomeID, @personEducationID, @personOccupationID, @customerNumberCarsOwned, @customerAddressLine, @customerPostalCodeID, @customerSalesTerritoryID, @customerPhone, @customerDateFirstPurchase);
        SET @customerID = SCOPE_IDENTITY();

        -- Insert the new user in the UserAccount table and capture the generated ID
        INSERT INTO Accounts.UserAccount
        (customerID, userAccountPasswordHash, userAccountQuestion, userAccountAnswer, userRoleID)
        VALUES
        (@customerID, @passwordHash, @userAccountQuestion, @answerHash, @userRoleID);
        SET @userAccountID = SCOPE_IDENTITY();

        -- Insert in the Monitoring.logSentEmail table
        INSERT INTO Monitoring.logSentEmail (userAccountID, logSentEmailMessage)
        VALUES (@userAccountID, @logMessage);
    END TRY
    BEGIN CATCH
        -- Reverse the transaction in case of an error
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @errorMessage VARCHAR(200) = ERROR_MESSAGE();
        EXEC Monitoring.sp_Log_Error -1, 'Accounts.UserAccount', 'INSERT', @errorMessage;
        THROW; -- rethrow
    END CATCH

    -- Confirm transaction
    IF @@TRANCOUNT > 0 COMMIT TRANSACTION;
END;
GO

-- Edit User Account Email
DROP PROCEDURE IF EXISTS Accounts.sp_Edit_User_Account_Email;
GO
CREATE PROCEDURE Accounts.sp_Edit_User_Account_Email
    @userAccountID INT,
    @customerEmailAddress VARCHAR(50)
AS
BEGIN
    BEGIN TRANSACTION;

    BEGIN TRY
        DECLARE @customerID INT;

        -- Check if the user exists
        IF NOT EXISTS (SELECT 1 FROM Accounts.UserAccount WHERE userAccountID = @userAccountID)
        BEGIN
            THROW 50000, 'The user does not exist.', 1;
        END
        SET @customerID = (SELECT customerID FROM Accounts.UserAccount WHERE userAccountID = @userAccountID);

        -- Check if the email is valid
        IF @customerEmailAddress IS NULL OR @customerEmailAddress = '' OR @customerEmailAddress NOT LIKE '%_@_%._%'
        BEGIN
            THROW 50000, 'The email is not valid.', 1;
        END
        IF EXISTS (SELECT 1 FROM Customers.Customer WHERE customerEmailAddress = @customerEmailAddress)
        BEGIN
            THROW 50000, 'The specified email is already in use.', 1;
        END

        -- Update the e-mail in the Customers.Customer table
        UPDATE Customers.Customer
        SET customerEmailAddress = @customerEmailAddress
        WHERE customerID = @customerID;

        PRINT 'User account email was updated successfully.'; -- for testing
    END TRY
    BEGIN CATCH
        -- Reverse the transaction in the event of an error
        ROLLBACK TRANSACTION;
        DECLARE @errorMessage VARCHAR(200) = ERROR_MESSAGE();
        EXEC Monitoring.sp_Log_Error -1, 'Customers.Customer', 'UPDATE', @errorMessage;
        THROW; -- rethrow
    END CATCH

    -- Confirm transaction
    IF @@TRANCOUNT > 0 COMMIT TRANSACTION;
END;
GO

--Remove User Account
DROP PROCEDURE IF EXISTS Accounts.sp_Remove_User_Account;
GO
CREATE PROCEDURE Accounts.sp_Remove_User_Account
    @userAccountID INT
AS
BEGIN
    BEGIN TRANSACTION;

    BEGIN TRY
        -- Check if the user exists
        IF NOT EXISTS (SELECT 1 FROM Accounts.UserAccount WHERE userAccountID = @userAccountID)
        BEGIN
            THROW 50000, 'The specified user does not exist.', 1; -- Raise an error
        END

        DECLARE @customerID INT = (SELECT customerID FROM Accounts.UserAccount WHERE userAccountID = @userAccountID);

        -- Inform user about the removal
        INSERT INTO Monitoring.logSentEmail (userAccountID, logSentEmailMessage)
        VALUES (@userAccountID, 'User account removed.');

        -- Remove the user from the Accounts.UserAccount table and Customers.Customer table
        -- Instead of deleting the record, we will append '_DELETED' to the email which is unique
        UPDATE Customers.Customer
        SET customerEmailAddress = customerEmailAddress + '_DELETED'
        WHERE customerID = @customerID;

        PRINT 'User account was removed successfully.'; -- for testing
    END TRY
    BEGIN CATCH
        -- Reverse the transaction in the event of an error
        ROLLBACK TRANSACTION;
        DECLARE @errorMessage VARCHAR(200) = ERROR_MESSAGE();
        EXEC Monitoring.sp_Log_Error -1, 'Accounts.UserAccount', 'DELETE', @errorMessage;
        THROW; -- rethrow
    END CATCH

    -- Confirm transaction
    IF @@TRANCOUNT > 0 COMMIT TRANSACTION;
END;
GO

DROP PROCEDURE IF EXISTS Accounts.sp_Account_Password_Recover;
GO
CREATE PROCEDURE Accounts.sp_Account_Password_Recover
    @customerEmailAddress VARCHAR(50),
    @userAccountQuestion VARCHAR(50),
    @userAccountAnswer VARCHAR(50),
    @newPassword VARCHAR(50)
AS
BEGIN
    BEGIN TRANSACTION;

    BEGIN TRY
        DECLARE @userAccountID INT;
        DECLARE @customerID INT;

        -- Check if the email is valid
        IF @customerEmailAddress IS NULL OR @customerEmailAddress NOT LIKE '%_@_%._%'
        BEGIN
            THROW 50000, 'The email is not valid.', 1; -- Raise an error
        END

        -- Check if the question is valid
        IF @userAccountQuestion IS NULL OR @userAccountQuestion = ''
        BEGIN
            THROW 50000, 'The specified question is not valid.', 1; -- Raise an error
        END

        -- Check if the answer is valid
        IF @userAccountAnswer IS NULL OR @userAccountAnswer = ''
        BEGIN
            THROW 50000, 'The specified answer is not valid.', 1; -- Raise an error
        END

        -- Check if the new password is valid
        IF @newPassword IS NULL OR @newPassword = ''
        BEGIN
            THROW 50000, 'The specified password is not valid.', 1; -- Raise an error
        END

        SET @customerID = (SELECT customerID FROM Customers.Customer WHERE customerEmailAddress = @customerEmailAddress);

        SELECT @userAccountID = userAccountID
        FROM Accounts.UserAccount
        WHERE customerID = @customerID
          AND userAccountQuestion = @userAccountQuestion
          AND userAccountAnswer = HASHBYTES('SHA2_256', @userAccountAnswer);

        -- Check that the data provided corresponds to a user
        IF @userAccountID IS NOT NULL
        BEGIN
            -- Update the password hash
            UPDATE Accounts.UserAccount
            SET userAccountPasswordHash = HASHBYTES('SHA2_256', @newPassword)
            WHERE userAccountID = @userAccountID;

            -- Add log to Monitoring.logSentEmail table
            INSERT INTO Monitoring.logSentEmail
            (userAccountID, logSentEmailMessage)
            VALUES
            (@userAccountID, 'Password recovery was successful.');

            PRINT 'Password recovery was successful.';
        END
        ELSE
        BEGIN
            EXEC Monitoring.sp_Log_Error -1, 'Accounts.UserAccount', 'UPDATE', 'Invalid data was entered, cannot recover password.';
            PRINT 'Invalid data was entered, cannot recover password.';
        END
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        DECLARE @errorMessage VARCHAR(200) = ERROR_MESSAGE();
        EXEC Monitoring.sp_Log_Error -1, 'Accounts.UserAccount', 'UPDATE', @errorMessage;
        PRINT 'Failed to recover the password: ' + @errorMessage;
        THROW;
    END CATCH

    -- Confirm transaction
    IF @@TRANCOUNT > 0 COMMIT TRANSACTION;
END;
GO

/*
    Sales
*/

-- Sales Information
DROP PROCEDURE IF EXISTS Sales.sp_Sales_Information;
GO
CREATE PROCEDURE Sales.sp_Sales_Information
    @saleDate DATETIME2,
    @customerID INT
AS
BEGIN
    BEGIN TRY
        -- Check if the customer exists
        IF NOT EXISTS (SELECT 1 FROM Customers.Customer WHERE customerID = @customerID)
        BEGIN
            THROW 50000, 'The customer does not exist.', 1;
        END

        -- Check if the sale date is valid
        IF TRY_CONVERT(DATETIME2, @saleDate) IS NULL OR @saleDate = '' OR @saleDate > GETDATE()
        BEGIN
            THROW 50000, 'The sale date is not valid.', 1;
        END

        -- Retrieve sales information
        SELECT
            so.salesOrderNumber,
            cur.currencyCode,
            st.salesTerritoryRegion,
            cou.countryName,
            so.salesOrderDate,
            so.salesOrderShipDate,
            so.salesOrderDueDate,
            sol.salesOrderLineNumber,
            p.productName,
            sol.salesOrderLineQuantity,
            sol.salesOrderLineUnitPrice,
            sol.salesOrderLineTaxAmt,
            sol.salesOrderLineFreight
        FROM Sales.SalesOrder AS so
        JOIN Sales.SalesOrderLine AS sol
            ON so.salesOrderID = sol.salesOrderID
        JOIN Products.Product AS p
            ON sol.productID = p.productID
        JOIN Sales.Currency AS cur
            ON so.currencyID = cur.currencyID
        JOIN Localization.SalesTerritory AS st
            ON so.salesTerritoryID = st.salesTerritoryID
        JOIN Localization.Country AS cou
            ON st.countryID = cou.countryID
        WHERE so.salesOrderDate = @saleDate AND so.customerID = @customerID;
    END TRY
    BEGIN CATCH
        DECLARE @errorMessage VARCHAR(200) = ERROR_MESSAGE();
        EXEC Monitoring.sp_Log_Error -1, 'Sales.SalesOrder', 'SELECT', @errorMessage;
        THROW;
    END CATCH
END;
GO

--Indexes

-- Índice vendas por cidade
CREATE NONCLUSTERED INDEX IDX_Sales_CityState 
ON Sales.SalesOrder (customerID);
GO

CREATE NONCLUSTERED INDEX IDX_Customer_PostalCode 
ON Customers.Customer (customerPostalCodeID);
GO

CREATE NONCLUSTERED INDEX IDX_PostalCode_City 
ON Localization.PostalCode (cityID);
GO

CREATE NONCLUSTERED INDEX IDX_City_State 
ON Localization.City (cityName, stateID);
GO

-- Pesquisa vendas por cidade
/*
SELECT 
    c.cityName AS Cidade,
    s.stateCode AS Codigo_Estado,
    SUM(sol.salesOrderLineUnitPrice) AS Total_Vendas
FROM 
    Sales.SalesOrderLine sol
JOIN 
    Sales.SalesOrder so ON sol.salesOrderID = so.salesOrderID
JOIN 
    Customers.Customer cu ON so.customerID = cu.customerID
JOIN 
    Localization.PostalCode pc ON cu.customerPostalCodeID = pc.postalCodeID
JOIN 
    Localization.City c ON pc.cityID = c.cityID
JOIN 
    Localization.State s ON c.stateID = s.stateID
GROUP BY 
    c.cityName, s.stateCode
ORDER BY 
    Total_Vendas DESC;
*/

-- Índice produtos associados a vendas com valor total superior a 1000
CREATE NONCLUSTERED INDEX IDX_Product_Sales 
ON Sales.SalesOrderLine (productID)
INCLUDE (salesOrderLineUnitPrice);
GO

CREATE NONCLUSTERED INDEX IDX_Product_Name 
ON Products.Product (productName);
GO

-- Pesquisa produtos associados a vendas com valor total superior a 1000
/*
SELECT 
    p.productName AS Produto,
    SUM(sol.salesOrderLineUnitPrice) AS Valor_Total
FROM 
    Sales.SalesOrderLine sol
JOIN 
    Products.Product p ON sol.productID = p.productID
GROUP BY 
    p.productName
HAVING 
    SUM(sol.salesOrderLineUnitPrice) > 1000
ORDER BY 
    Valor_Total DESC;
*/

-- Índice produtos vendidos por categoria
CREATE NONCLUSTERED INDEX IDX_Sales_Product 
ON Sales.SalesOrderLine (productID);
GO

CREATE NONCLUSTERED INDEX IDX_Product_Model 
ON Products.Product (modelID);
GO

CREATE NONCLUSTERED INDEX IDX_Model_Subcategory 
ON Products.Model (productSubcategoryID);
GO

CREATE NONCLUSTERED INDEX IDX_Product_Category 
ON Products.ProductSubcategory (productCategoryID);
GO

CREATE NONCLUSTERED INDEX IDX_Category_Name 
ON Products.ProductCategory (productCategoryName);
GO

-- Pesquisa produtos vendidos por categoria
/*
SELECT 
    pc.productCategoryName AS Categoria,
    COUNT(sol.productID) AS Numero_Produtos_Vendidos
FROM 
    Sales.SalesOrderLine sol
JOIN 
    Products.Product p ON sol.productID = p.productID
JOIN 
    Products.Model m ON p.modelID = m.modelID
JOIN 
    Products.ProductSubcategory ps ON m.productSubcategoryID = ps.productSubcategoryID
JOIN 
    Products.ProductCategory pc ON ps.productCategoryID = pc.productCategoryID
GROUP BY 
    pc.productCategoryName
ORDER BY 
    Numero_Produtos_Vendidos DESC;
*/