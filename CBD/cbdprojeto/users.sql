/*
    FILE 6
    Test the procedures of the User Accounts and related tables

    PascalCase for table names
    camelCase for column names
    Type_Snake_Case for constraints, functions, procedures, triggers, views, etc.

    @author: Jose Oliveira; Diogo Oliveira
 */

USE AdventureWorks;
GO

-- Criar role
EXEC Accounts.sp_Add_User_Role 'Admin', 'db_owner';
GO

-- Criar user account
EXEC Accounts.sp_Add_User_Account
    @personTitleID = null, -- can be null
    @customerFirstName = 'Diogo',
    @customerMiddleName = null, -- can be null
    @customerLastName = 'Oliveira',
    @customerBirthDate = '2012-12-29',
    @personMaritalStatusID = 1,
    @personGenderID = 1,
    @customerEmailAddress = 'email@teste.com',
    @personYearlyIncomeID = 1,
    @personEducationID = 1,
    @personOccupationID = 1,
    @customerNumberCarsOwned = 20, -- 0 to 255
    @customerAddressLine = 'Rua do teste',
    @customerPostalCodeID = 1,
    @customerSalesTerritoryID = 1,
    @customerPhone = '34822932', -- has duplicates
    @customerDateFirstPurchase = '2022-12-29',
    @userAccountPassword = 'PasswordSegura24',
    @userAccountQuestion = 'Qual o nome do seu primeiro animal de estimação?',
    @userAccountAnswer = 'Rex',
    @userRoleID = 1;
GO

SELECT * FROM Accounts.UserAccount;
SELECT * FROM Customers.Customer WHERE customerID = (SELECT customerID FROM Accounts.UserAccount WHERE userAccountID = 1);

-- Editar email
EXEC Accounts.sp_Edit_User_Account_Email
    @customerEmailAddress = 'email2@teste.com',
    @userAccountID = 1;
GO

-- Editar password
EXEC Accounts.sp_Account_Password_Recover
    @customerEmailAddress = 'email2@teste.com', -- since we changed the email
    @userAccountQuestion = 'Qual o nome do seu primeiro animal de estimação?',
    @userAccountAnswer = 'Rex',
    @newPassword = 'PasswordSegura25';
GO

-- Remover user account
EXEC Accounts.sp_Remove_User_Account
    @userAccountID = 1;
GO

-- See sales information
EXEC Sales.sp_Sales_Information
    @saleDate = '2010-12-29',
    @customerID = 4123;
GO

SELECT * FROM Customers.Customer where customerID= 4123;
SELECT * FROM Products.Product where productID= 288;

---------------------------------------------------------------------------
-- Encrypt email by key
USE AdventureWorks;
GO

CREATE MASTER KEY ENCRYPTION BY PASSWORD = 'StrongPassword123!';
GO

CREATE CERTIFICATE EncryptCert WITH SUBJECT = 'Encryption Certificate';
GO

CREATE SYMMETRIC KEY EncryptKey WITH ALGORITHM = AES_256 ENCRYPTION BY CERTIFICATE EncryptCert;
GO

ALTER TABLE Customers.Customer ADD customerEmailAddressEncrypted VARBINARY(256);
GO
OPEN SYMMETRIC KEY EncryptKey DECRYPTION BY CERTIFICATE EncryptCert;
UPDATE Customers.Customer SET customerEmailAddressEncrypted = ENCRYPTBYKEY(KEY_GUID('EncryptKey'), customerEmailAddress);
GO
CLOSE SYMMETRIC KEY EncryptKey;
GO
ALTER TABLE Customers.Customer DROP COLUMN customerEmailAddress;
GO

-- Test
SELECT * FROM Customers.Customer;

-- Decrypt email by key
OPEN SYMMETRIC KEY EncryptKey DECRYPTION BY CERTIFICATE EncryptCert;
SELECT CONVERT(VARCHAR(50), DECRYPTBYKEY(customerEmailAddressEncrypted)) AS customerEmailAddress FROM Customers.Customer;
GO
CLOSE SYMMETRIC KEY EncryptKey;
GO

----------------------------------------------------------------------------

select * FROM Accounts.UserAccount;
SELECT * FROM Accounts.UserRole;

-- Access

-- Criação dos logins
CREATE LOGIN AdminUser WITH PASSWORD = 'StrongPassword123!';
CREATE LOGIN SalesPersonUser WITH PASSWORD = 'StrongPassword123!';
CREATE LOGIN SalesTerritoryUser WITH PASSWORD = 'StrongPassword123!';

-- Criação dos utilizadores na BD
USE AdventureWorks;
GO

CREATE USER AdminUser FOR LOGIN AdminUser;
CREATE USER SalesPersonUser FOR LOGIN SalesPersonUser;
CREATE USER SalesTerritoryUser FOR LOGIN SalesTerritoryUser;

ALTER ROLE db_owner ADD MEMBER AdminUser;


-- Permitir acesso total às tabelas de vendas
GRANT SELECT, INSERT, UPDATE, DELETE ON Sales.SalesOrder TO SalesPersonUser;
GRANT SELECT, INSERT, UPDATE, DELETE ON Sales.SalesOrderLine TO SalesPersonUser;
GRANT SELECT ON Customers.Customer TO SalesPersonUser;


-- Permitir apenas SELECT em todas as outras tabelas
GRANT SELECT ON SCHEMA::Customers TO SalesPersonUser;
GRANT SELECT ON SCHEMA::Localization TO SalesPersonUser;
GRANT SELECT ON SCHEMA::Products TO SalesPersonUser;
GRANT SELECT ON SCHEMA::Accounts TO SalesPersonUser;


-- View para informações do território "Rocky Mountain"
SELECT *
FROM Localization.SalesTerritory
WHERE salesTerritoryRegion = 'Rocky Mountain';

-- Login AdminUser
EXECUTE AS USER = 'AdminUser';

-- Teste: Acesso total a uma tabela
SELECT * FROM Sales.SalesOrder; -- Deve retornar resultados
INSERT INTO Sales.SalesOrder (salesOrderDate, customerID) VALUES (GETDATE(), 1); -- Deve funcionar

-- Reverter o contexto
REVERT;



-- Login SalesPersonUser
EXECUTE AS USER = 'SalesPersonUser';

-- Teste: Acesso total às tabelas de vendas
SELECT * FROM Sales.SalesOrder; -- Deve retornar resultados
INSERT INTO Sales.SalesOrder (salesOrderDate, customerID) VALUES (GETDATE(), 1); -- Deve funcionar
DELETE FROM Sales.SalesOrder WHERE salesOrderID = 1; -- Deve funcionar

-- Teste: Apenas leitura em outras tabelas
SELECT * FROM Customers.Customer; -- Deve funcionar
UPDATE Customers.Customer SET customerFirstName = 'Test' WHERE customerID = 1; -- Deve falhar

select * FROM Customers.Customer
where customerFirstName = 'Test';

-- Reverter o contexto
REVERT;

-- Login SalesTerritoryUser
EXECUTE AS USER = 'SalesTerritoryUser';

-- Teste: Acesso negado à tabela completa
SELECT * FROM Localization.SalesTerritory; -- Deve falhar

-- Reverter o contexto
REVERT;
