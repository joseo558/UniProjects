/*
    FILE 3
    Migrate data from AdventureWorksLegacy to AdventureWorks
    This script assumes that the AdventureWorksLegacy and AdventureWorks databases are on the same server

    Migration is one-time only so no stored procedures are created

    AdventureWorksLegacy is the legacy (temporary) database that we are migrating data from, with the data from the excel files (in dbo schema)
        DROP DATABASE IF EXISTS AdventureWorksLegacy;
        GO
        CREATE DATABASE AdventureWorksLegacy;
        GO
        USE AdventureWorksLegacy;
        GO
        -- Import excel files using an IDE or SSMS to dbo schema
    AdventureWorks is the new database that we are migrating data to (see create.sql file)

    PascalCase for table names
    camelCase for column names
    Type_Snake_Case for constraints, functions, procedures, triggers, views, etc.

    Docker/DataGrip -> no $ at end of AdventureWorksLegacy.dbo.* tables

    @author: Jose Oliveira; Diogo Oliveira
 */

-- Use the new database
USE AdventureWorks;
GO

/*
    To reset the identity seed and delete data if something goes wrong

    USE AdventureWorks;
    DELETE FROM Localization.Continent;
    GO
    DBCC CHECKIDENT ('Localization.Continent', RESEED); -- third parameter can be 0 for delete, 1 after a truncate
    GO

    -- To check id:  DBCC CHECKIDENT ('Localization.State', NORESEED);
 */

/*
    Localization
 */

-- Localization.Continent
-- Cast text to varchar(20) for comparison
-- Skip the 'NA' continent invalid data
INSERT INTO Localization.Continent (continentName)
    SELECT DISTINCT CAST(SalesTerritoryGroup AS VARCHAR(20)) AS s
    FROM AdventureWorksLegacy.dbo.SalesTerritory$
    WHERE CAST(SalesTerritoryGroup AS VARCHAR(20)) != 'NA'
    ORDER BY s;
GO

-- Localization.Country
INSERT INTO Localization.Country (countryCode, countryName, continentID)
    SELECT DISTINCT CAST(lc.CountryRegionCode AS CHAR(2)),
                    CAST(lc.CountryRegionName AS VARCHAR(50)) AS rn,
                    (SELECT continentID FROM AdventureWorks.Localization.Continent WHERE continentName = CAST(lst.SalesTerritoryGroup AS VARCHAR(20)) COLLATE Latin1_General_100_CI_AI_SC_UTF8)
    FROM AdventureWorksLegacy.dbo.Customer$ AS lc
    JOIN AdventureWorksLegacy.dbo.SalesTerritory$ AS lst
    ON CAST(lc.CountryRegionName AS VARCHAR(50)) = CAST(lst.SalesTerritoryCountry AS VARCHAR(50))
    ORDER BY rn;
GO

-- Localization.State
INSERT INTO Localization.State (stateCode, stateName, countryID)
    SELECT DISTINCT CAST(lc.StateProvinceCode AS VARCHAR(5)),
                    CAST(lc.StateProvinceName AS VARCHAR(50)) AS sn,
                    (SELECT countryID FROM AdventureWorks.Localization.Country WHERE countryCode = CAST(lc.CountryRegionCode AS CHAR(2)) COLLATE Latin1_General_100_CI_AI_SC_UTF8)
    FROM AdventureWorksLegacy.dbo.Customer$ AS lc
    UNION ALL -- to add Berlin and be ordered with the rest
    SELECT 'BE', 'Berlin',
        (SELECT countryID FROM AdventureWorks.Localization.Country WHERE countryCode = 'DE')
    ORDER BY sn;
GO

-- Localization.City
DECLARE @cityName VARCHAR(50);
DECLARE @stateCode VARCHAR(5);

DECLARE cityCursor CURSOR FOR
    SELECT DISTINCT CAST(lc.City AS VARCHAR(50)) AS c,
                    CAST(lc.StateProvinceCode AS VARCHAR(5))
    FROM AdventureWorksLegacy.dbo.Customer$ AS lc
    ORDER BY c;

OPEN cityCursor;
FETCH NEXT FROM cityCursor INTO @cityName, @stateCode;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Validate Data
    -- Skip
    WHILE(@cityName = 'Frankfurt')
        BEGIN
            FETCH NEXT FROM cityCursor INTO @cityName, @stateCode; -- skip invalid city name
        END
    -- Fix just non-ascii accents
    IF(@cityName = 'S?vres')
        SET @cityName = 'Sèvres';
    IF(@cityName = 'Saarbr?cken')
        SET @cityName = 'Saarbrücken';
    -- Fix non-ascii and incorrect state
    IF(@cityName = 'M?nchen')
        BEGIN
            SET @cityName = 'München';
            SET @stateCode = 'BY';
        END
    IF(@cityName = 'M?nster')
        BEGIN
            SET @cityName = 'Münster';
            SET @stateCode = 'NW';
        END
    IF(@cityName = 'M?hlheim')
        BEGIN
            SET @cityName = 'Mühlheim';
            SET @stateCode = 'NW';
        END
    -- Fix incorrect state
    IF(@cityName = 'Berlin') SET @stateCode = 'BE';
    IF(@cityName = 'Bonn') SET @stateCode = 'NW';
    IF(@cityName = 'Burbank') SET @stateCode = 'CA';
    IF(@cityName = 'Columbus') SET @stateCode = 'OH';
    IF(@cityName = 'Frankfurt am Main') SET @stateCode = 'HE'; -- real name
    IF(@cityName = 'Hamburg') SET @stateCode = 'HH';
    IF(@cityName = 'Paderborn') SET @stateCode = 'NW';
    -- Saint-Ouen exists in 3 different regions in France, it is valid

    -- Insert Data
    IF NOT EXISTS (SELECT 1 FROM Localization.City WHERE cityName = @cityName
        AND stateID = (SELECT stateID FROM AdventureWorks.Localization.State WHERE stateCode = @stateCode COLLATE Latin1_General_100_CI_AI_SC_UTF8)
    )
    BEGIN
        INSERT INTO Localization.City (cityName, stateID)
        SELECT @cityName,
                (SELECT stateID FROM AdventureWorks.Localization.State WHERE stateCode = @stateCode COLLATE Latin1_General_100_CI_AI_SC_UTF8);
    END
    FETCH NEXT FROM cityCursor INTO @cityName, @stateCode;
END
CLOSE cityCursor;
DEALLOCATE cityCursor;
GO

-- Localization.PostalCode
DECLARE @postalCode VARCHAR(20),
    @cityName VARCHAR(50),
    @stateName VARCHAR(50);

DECLARE postalCodeCursor CURSOR FOR
    SELECT DISTINCT CAST(lc.postalCode AS VARCHAR(20)) AS pc,
                    CAST(lc.City AS VARCHAR(50)),
                    CAST(lc.StateProvinceName AS VARCHAR(50))
    FROM AdventureWorksLegacy.dbo.Customer$ AS lc
    ORDER BY pc;

OPEN postalCodeCursor;
FETCH NEXT FROM postalCodeCursor INTO @postalCode, @cityName, @stateName;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Validate Data
    -- Cities
    IF(@cityName = 'Frankfurt')
        SET @cityName = 'Frankfurt am Main'; -- real name
    -- Fix just non-ascii accents
    IF(@cityName = 'S?vres')
        SET @cityName = 'Sèvres';
    IF(@cityName = 'Saarbr?cken')
        SET @cityName = 'Saarbrücken';
    -- Fix non-ascii and incorrect state
    IF(@cityName = 'M?nchen')
        BEGIN
            SET @cityName = 'München';
            SET @stateName = 'Bayern';
        END
    IF(@cityName = 'M?nster')
        BEGIN
            SET @cityName = 'Münster';
            SET @stateName = 'Nordrhein-Westfalen';
        END
    IF(@cityName = 'M?hlheim')
        BEGIN
            SET @cityName = 'Mühlheim';
            SET @stateName = 'Nordrhein-Westfalen';
        END
    -- Fix incorrect state
    IF(@cityName = 'Berlin') SET @stateName = 'Berlin';
    IF(@cityName = 'Bonn') SET @stateName = 'Nordrhein-Westfalen';
    IF(@cityName = 'Burbank') SET @stateName = 'California';
    IF(@cityName = 'Columbus') SET @stateName = 'Ohio';
    IF(@cityName = 'Frankfurt am Main') SET @stateName = 'Hessen'; -- real name
    IF(@cityName = 'Hamburg') SET @stateName = 'Hamburg';
    IF(@cityName = 'Paderborn') SET @stateName = 'Nordrhein-Westfalen';
    -- Saint-Ouen exists in 3 different regions in France, it is valid

    -- Insert Data
    IF NOT EXISTS (SELECT 1 FROM Localization.PostalCode WHERE postalCode = @postalCode)
    BEGIN
        INSERT INTO Localization.PostalCode (postalCode, cityID)
        SELECT @postalCode,
                (SELECT cityID FROM AdventureWorks.Localization.City AS c
                               WHERE c.cityName = @cityName COLLATE Latin1_General_100_CI_AI_SC_UTF8
                                  AND c.stateID = (
                                    SELECT stateID FROM AdventureWorks.Localization.State WHERE stateName = @stateName COLLATE Latin1_General_100_CI_AI_SC_UTF8
                                  )
                );
    END

    FETCH NEXT FROM postalCodeCursor INTO @postalCode, @cityName, @stateName;
END
CLOSE postalCodeCursor;
DEALLOCATE postalCodeCursor;
GO

-- Localization.SalesTerritory
INSERT INTO Localization.SalesTerritory (salesTerritoryRegion, countryID)
    SELECT DISTINCT CAST(lst.SalesTerritoryRegion AS VARCHAR(50)) AS str,
                    (SELECT countryID FROM AdventureWorks.Localization.Country WHERE countryName = CAST(lst.SalesTerritoryCountry AS VARCHAR(50)) COLLATE Latin1_General_100_CI_AI_SC_UTF8)
    FROM AdventureWorksLegacy.dbo.SalesTerritory$ AS lst
    WHERE CAST(SalesTerritoryRegion AS VARCHAR(50)) != 'NA'
    ORDER BY str;
GO


/*
    Product
 */

-- Products.ProductCategory
INSERT INTO Products.ProductCategory (productCategoryName)
    SELECT DISTINCT CAST(lp.EnglishProductCategoryName AS VARCHAR(50)) AS pc
    FROM AdventureWorksLegacy.dbo.Products$ AS lp
    ORDER BY pc;
GO

-- Products.ProductSubcategory
INSERT INTO Products.ProductSubcategory (productSubcategoryName, productCategoryID)
    SELECT DISTINCT CAST(lpsc.EnglishProductSubcategoryName AS VARCHAR(50)) AS psc,
                    (SELECT productCategoryID FROM AdventureWorks.Products.ProductCategory WHERE productCategoryName = CAST(lp.EnglishProductCategoryName AS VARCHAR(50)) COLLATE Latin1_General_100_CI_AI_SC_UTF8)
    FROM AdventureWorksLegacy.dbo.ProductSubCategory$ AS lpsc
    JOIN AdventureWorksLegacy.dbo.Products$ AS lp
    ON lpsc.ProductSubcategoryKey = lp.ProductSubcategoryKey
    ORDER BY psc;
GO

-- Products.Color
INSERT INTO Products.Color (colorName)
    SELECT DISTINCT CAST(lp.Color AS VARCHAR(50)) AS c
    FROM AdventureWorksLegacy.dbo.Products$ AS lp
    WHERE CAST(lp.Color AS VARCHAR(50)) != 'NA'
    ORDER BY c;
GO

-- Products.ModelStyle
INSERT INTO Products.ModelStyle (modelStyleCode, modelStyleName)
    SELECT DISTINCT CAST(lp.Style AS CHAR(1)) AS msc,
                    CASE
                        WHEN CAST(lp.Style AS CHAR(1)) = 'M' THEN 'Men'
                        WHEN CAST(lp.Style AS CHAR(1)) = 'U' THEN 'Unisex'
                        WHEN CAST(lp.Style AS CHAR(1)) = 'W' THEN 'Woman'
                        ELSE 'Unknown' -- just in case
                    END
    FROM AdventureWorksLegacy.dbo.Products$ AS lp
    WHERE CAST(lp.Style AS CHAR(1)) != '' -- skip empty style
    ORDER BY msc;
GO

-- Products.ModelClass
INSERT INTO Products.ModelClass (modelClassCode, modelClassName)
    SELECT DISTINCT CAST(lp.Class AS CHAR(1)) AS mcc,
                    CASE
                        WHEN CAST(lp.Class AS CHAR(1)) = 'H' THEN 'High'
                        WHEN CAST(lp.Class AS CHAR(1)) = 'M' THEN 'Medium'
                        WHEN CAST(lp.Class AS CHAR(1)) = 'L' THEN 'Low'
                        ELSE 'Unknown' -- just in case
                    END
    FROM AdventureWorksLegacy.dbo.Products$ AS lp
    WHERE CAST(lp.Class AS CHAR(1)) != '' -- skip empty class
    ORDER BY mcc;
GO

-- Products.ProductLine
INSERT INTO Products.ProductLine (productLineCode, productLineName)
    SELECT DISTINCT CAST(lp.ProductLine AS CHAR(1)) AS plc,
                    CASE
                        WHEN CAST(lp.ProductLine AS CHAR(1)) = 'M' THEN 'Mountain'
                        WHEN CAST(lp.ProductLine AS CHAR(1)) = 'R' THEN 'Road'
                        WHEN CAST(lp.ProductLine AS CHAR(1)) = 'T' THEN 'Touring'
                        WHEN CAST(lp.ProductLine AS CHAR(1)) = 'S' THEN 'Sport'
                        ELSE 'Unknown' -- just in case
                    END
    FROM AdventureWorksLegacy.dbo.Products$ AS lp
    WHERE CAST(lp.ProductLine AS CHAR(1)) != '' -- skip empty product line
    ORDER BY plc;
GO

-- Products.SizeUnit
INSERT INTO Products.SizeUnit (sizeUnitCode, sizeUnitName)
    VALUES ('CM', 'Centimeters'); -- there is only cm in the legacy database
GO

-- Products.SizeRange
INSERT INTO Products.SizeRange (sizeRangeMin, sizeRangeMax, sizeUnitID)
    -- SUBSTRING(string, start, length)
    SELECT DISTINCT CAST( SUBSTRING(lp.SizeRange, 0, CHARINDEX('-', lp.SizeRange)) AS TINYINT) AS srmin,
                    CAST( SUBSTRING(lp.SizeRange, CHARINDEX('-', lp.SizeRange) + 1, CHARINDEX(' ', lp.SizeRange) - CHARINDEX('-', lp.SizeRange) - 1) AS TINYINT) AS srmax,
                    (SELECT sizeUnitID FROM AdventureWorks.Products.SizeUnit WHERE sizeUnitCode = 'CM')
    FROM AdventureWorksLegacy.dbo.Products$ AS lp
    WHERE lp.SizeRange LIKE '%-%' -- skip invalid range
    ORDER BY srmin, srmax;
GO

-- Products.WeightUnit
INSERT INTO Products.WeightUnit (weightUnitCode, weightUnitName) VALUES
    ('G', 'Grams'),
    ('LB', 'Pounds'); -- there is only G and LB in the legacy database
GO

-- Products.Model
-- To process the data in a more readable way will use a cursor
DECLARE @modelName VARCHAR(50),
    @modelDescription VARCHAR(255), -- is EnglishDescription
    @productSubcategory VARCHAR(50),
    @modelStyle CHAR(1),
    @modelClass CHAR(1),
    @productLine CHAR(1),
    @sizeUnit CHAR(2),
    @weightUnit CHAR(2);

DECLARE modelCursor CURSOR FOR
    SELECT DISTINCT CAST(lp.ModelName AS VARCHAR(50)) AS mn,
                    CAST(lp.EnglishDescription AS VARCHAR(255)),
                    CAST(lpsc.EnglishProductSubcategoryName AS VARCHAR(50)),
                    CAST(lp.Style AS CHAR(1)),
                    CAST(lp.Class AS CHAR(1)),
                    CAST(lp.ProductLine AS CHAR(1)),
                    CAST(lp.SizeUnitMeasureCode AS CHAR(2)),
                    CAST(lp.WeightUnitMeasureCode AS CHAR(2))
    FROM AdventureWorksLegacy.dbo.Products$ AS lp
    JOIN AdventureWorksLegacy.dbo.ProductSubCategory$ AS lpsc
        ON lp.ProductSubcategoryKey = lpsc.ProductSubcategoryKey
    ORDER BY mn;

OPEN modelCursor;
FETCH NEXT FROM modelCursor INTO
    @modelName, @modelDescription, @productSubcategory, @modelStyle, @modelClass, @productLine, @sizeUnit, @weightUnit;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Validate Data
    -- Model name
    WHILE(@modelName = 'ML Mountain Frame-W' AND @modelStyle = 'U')
        BEGIN
            -- Skip wrong duplicate data
            FETCH NEXT FROM modelCursor INTO @modelName, @modelDescription, @productSubcategory, @modelStyle, @modelClass, @productLine, @sizeUnit, @weightUnit;
        END
    -- Description and style
    IF(@modelDescription IS NULL) -- Rear Brakes has null description
        SET @modelDescription = @modelName;
    IF(@modelName = 'ML Mountain Frame') -- men version
        BEGIN
            SET @modelDescription = REPLACE(@modelDescription, 'Women', 'Men');
            SET @modelStyle = 'M';
        END
    IF(@modelName = 'ML Mountain Frame-W' AND @modelStyle = 'W') -- woman version
        BEGIN
            SET @modelDescription = REPLACE(@modelDescription, 'Men', 'Women');
        END
    IF(@modelName = 'HL Touring Frame') -- is man version
        BEGIN
            SET @modelStyle = 'M';
        END
    IF(@modelName = 'ML Road Frame') -- men version
        BEGIN
            SET @modelStyle = 'M';
        END
    -- Product line
    IF(@modelName = 'ML Road Seat/Saddle 2') -- road product line
        BEGIN
            SET @productLine = 'R';
        END

    -- Insert Data
    INSERT INTO Products.Model (modelName, modelDescription, productSubcategoryID, modelStyleID, modelClassID, productLineID, sizeUnitID, weightUnitID)
    SELECT @modelName,
           @modelDescription,
            (SELECT productSubcategoryID FROM AdventureWorks.Products.ProductSubcategory WHERE productSubcategoryName = @productSubcategory),
           (SELECT modelStyleID FROM AdventureWorks.Products.ModelStyle WHERE modelStyleCode = @modelStyle),
           (SELECT modelClassID FROM AdventureWorks.Products.ModelClass WHERE modelClassCode = @modelClass),
           (SELECT productLineID FROM AdventureWorks.Products.ProductLine WHERE productLineCode = @productLine),
           (SELECT sizeUnitID FROM AdventureWorks.Products.SizeUnit WHERE sizeUnitCode = @sizeUnit),
           (SELECT weightUnitID FROM AdventureWorks.Products.WeightUnit WHERE weightUnitCode = @weightUnit);

    FETCH NEXT FROM modelCursor INTO @modelName, @modelDescription, @productSubcategory, @modelStyle, @modelClass, @productLine, @sizeUnit, @weightUnit;
END
CLOSE modelCursor;
DEALLOCATE modelCursor;
GO

-- Products.Product
-- To process the data in a more readable way will use a cursor
DECLARE @productName VARCHAR(50),
    @modelName VARCHAR(50),
    @colorName VARCHAR(50),
    @productSafetyStockLevel INT,
    @productStandardCost DECIMAL(16, 4),
    @productListPrice DECIMAL(16, 4),
    @productDealerPrice DECIMAL(16, 4),
    @productSize VARCHAR(5),
    @productSizeRange VARCHAR(50),
    @productWeight DECIMAL(16, 4),
    @productDaysToManufacture SMALLINT;

DECLARE productCursor CURSOR FOR
    SELECT CAST(lp.EnglishProductName AS VARCHAR(50)),
           CAST(lp.ModelName AS VARCHAR(50)) AS mn,
           CAST(lp.Color AS VARCHAR(50)) AS cn,
           TRY_CAST(lp.SafetyStockLevel AS INT),
           TRY_CAST( REPLACE( TRY_CAST(lp.StandardCost AS VARCHAR(50)), ',', '.') AS DECIMAL(16, 4)), -- 2 lines are null, we could default to 0 but we'll just accept nulls
           TRY_CAST( REPLACE( TRY_CAST(lp.ListPrice AS VARCHAR(50)), ',', '.') AS DECIMAL(16, 4)), -- can't cast text to decimal (so double cast, Microsoft issues)
           TRY_CAST( REPLACE( TRY_CAST(lp.DealerPrice AS VARCHAR(50)), ',', '.') AS DECIMAL(16, 4)),
           CAST(lp.Size AS VARCHAR(5)) AS sn, -- S, M, a number or whatever
           CAST(lp.SizeRange AS VARCHAR(50)),
           TRY_CAST( TRY_CAST(lp.Weight AS VARCHAR(50)) AS DECIMAL(16, 4)),
           TRY_CAST( TRY_CAST(lp.DaysToManufacture AS VARCHAR(10)) AS SMALLINT)
    FROM AdventureWorksLegacy.dbo.Products$ AS lp
    ORDER BY mn, cn, sn;

OPEN productCursor;
FETCH NEXT FROM productCursor INTO
    @productName, @modelName, @colorName, @productSafetyStockLevel, @productStandardCost, @productListPrice, @productDealerPrice, @productSize, @productSizeRange, @productWeight, @productDaysToManufacture;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Validate Data
    -- Model name
    IF(@productName = 'ML Mountain Frame - Black, 38')
        BEGIN
            SET @modelName = 'ML Mountain Frame'; -- man version
        END

    -- Insert Data
    INSERT INTO Products.Product (productName, modelID, colorID, productSafetyStockLevel, productStandardCost, productListPrice, productDealerPrice, productSize, productSizeRangeID, productWeight, productDaysToManufacture)
    SELECT @productName,
           (SELECT modelID FROM AdventureWorks.Products.Model WHERE modelName = @modelName),
           (SELECT colorID FROM AdventureWorks.Products.Color WHERE colorName = @colorName),
           @productSafetyStockLevel,
           @productStandardCost,
           @productListPrice,
           @productDealerPrice,
           @productSize,
           CASE
               WHEN @productSizeRange IS NULL THEN NULL
               ELSE (SELECT sizeRangeID FROM AdventureWorks.Products.SizeRange
                                        WHERE @productSizeRange LIKE '%-%'
                                          AND sizeRangeMin = CAST( SUBSTRING(@productSizeRange, 0, CHARINDEX('-', @productSizeRange)) AS TINYINT)
                                          AND sizeRangeMax = CAST( SUBSTRING(@productSizeRange, CHARINDEX('-', @productSizeRange) + 1, CHARINDEX(' ', @productSizeRange) - CHARINDEX('-', @productSizeRange) - 1) AS TINYINT)
                                        )
           END,
           @productWeight,
           @productDaysToManufacture;

    FETCH NEXT FROM productCursor INTO
        @productName, @modelName, @colorName, @productSafetyStockLevel, @productStandardCost, @productListPrice, @productDealerPrice, @productSize, @productSizeRange, @productWeight, @productDaysToManufacture;
END
CLOSE productCursor;
DEALLOCATE productCursor;
GO

/*
    Customer
 */

-- Customers.PersonTitle
INSERT INTO Customers.PersonTitle (personTitleName)
    SELECT DISTINCT REPLACE( CAST(lc.Title AS VARCHAR(20)), '.', '') AS pt
    FROM AdventureWorksLegacy.dbo.Customer$ AS lc
    WHERE CAST(lc.Title AS VARCHAR(20)) != ''
    ORDER BY pt;
GO

-- Customers.PersonGender
INSERT INTO Customers.PersonGender (personGenderCode, personGenderName) VALUES
    ('M', 'Male'), -- Only M and F in legacy database
    ('F', 'Female'),
    ('N', 'Non-Binary');
GO

-- Customers.PersonMaritalStatus
INSERT INTO Customers.PersonMaritalStatus (personMaritalStatusCode, personMaritalStatusName) VALUES
    ('S', 'Single'), -- only S and M in legacy database
    ('M', 'Married'),
    ('D', 'Divorced'),
    ('W', 'Widowed');
GO

-- Customers.PersonYearlyIncome
INSERT INTO Customers.PersonYearlyIncome (personYearlyIncomeRange) VALUES
(10000), (20000), (30000), (40000), (50000), (60000), (70000), (80000), (90000), (100000), (110000), (120000), (130000), (140000), (150000), (160000), (170000);
GO

-- Customers.PersonEducation
INSERT INTO Customers.PersonEducation (personEducationName)
    SELECT DISTINCT CAST(lc.Education AS VARCHAR(50)) AS pe
    FROM AdventureWorksLegacy.dbo.Customer$ AS lc
    WHERE CAST(lc.Education AS VARCHAR(50)) != ''
    ORDER BY pe;
GO

-- Customers.PersonOccupation
INSERT INTO Customers.PersonOccupation (personOccupationName)
    SELECT DISTINCT CAST(lc.Occupation AS VARCHAR(50)) AS po
    FROM AdventureWorksLegacy.dbo.Customer$ AS lc
    WHERE CAST(lc.Occupation AS VARCHAR(50)) != ''
    ORDER BY po;
GO

-- Customers.Customer
INSERT INTO Customers.Customer (personTitleID, customerFirstName, customerMiddleName, customerLastName, customerBirthDate, personMaritalStatusID, personGenderID, customerEmailAddress, personYearlyIncomeID, personEducationID, personOccupationID, customerNumberCarsOwned, customerAddressLine, customerPostalCodeID, customerSalesTerritoryID, customerPhone, customerDateFirstPurchase)
    SELECT
        (SELECT personTitleID FROM AdventureWorks.Customers.PersonTitle AS cpt WHERE personTitleName = REPLACE( CAST(lc.Title AS VARCHAR(20)), '.', '') COLLATE Latin1_General_100_CI_AI_SC_UTF8),
        CAST(lc.FirstName AS VARCHAR(50)) AS lcfn,
        REPLACE( CAST(lc.MiddleName AS VARCHAR(50)), '.', '') AS lcmn,
        CAST(lc.LastName AS VARCHAR(50)) AS lcln,
        CONVERT(DATE, CAST(lc.BirthDate AS VARCHAR(20)), 103), -- 103 is dd/MM/yyyy to SQL Date (yyyy-MM-dd)
        (SELECT personMaritalStatusID FROM AdventureWorks.Customers.PersonMaritalStatus WHERE personMaritalStatusCode = CAST(lc.MaritalStatus AS CHAR(1)) COLLATE Latin1_General_100_CI_AI_SC_UTF8),
        (SELECT personGenderID FROM AdventureWorks.Customers.PersonGender WHERE personGenderCode = CAST(lc.Gender AS CHAR(1)) COLLATE Latin1_General_100_CI_AI_SC_UTF8),
        CAST(lc.EmailAddress AS VARCHAR(50)),
        (SELECT personYearlyIncomeID FROM AdventureWorks.Customers.PersonYearlyIncome WHERE personYearlyIncomeRange = CAST(CAST(lc.YearlyIncome AS VARCHAR(50)) AS INT)),
        (SELECT personEducationID FROM AdventureWorks.Customers.PersonEducation WHERE personEducationName = CAST(lc.Education AS VARCHAR(50)) COLLATE Latin1_General_100_CI_AI_SC_UTF8),
        (SELECT personOccupationID FROM AdventureWorks.Customers.PersonOccupation WHERE personOccupationName = CAST(lc.Occupation AS VARCHAR(50)) COLLATE Latin1_General_100_CI_AI_SC_UTF8),
        CAST(CAST(lc.NumberCarsOwned AS VARCHAR(50)) AS TINYINT),
        CAST(lc.AddressLine1 AS VARCHAR(50)),
        (SELECT postalCodeID FROM AdventureWorks.Localization.PostalCode WHERE postalCode = CAST(lc.PostalCode AS VARCHAR(20)) COLLATE Latin1_General_100_CI_AI_SC_UTF8),
        (SELECT salesTerritoryID FROM AdventureWorks.Localization.SalesTerritory WHERE salesTerritoryRegion = CAST(lst.SalesTerritoryRegion AS VARCHAR(50)) COLLATE Latin1_General_100_CI_AI_SC_UTF8),
        CAST(lc.Phone AS VARCHAR(20)),
        CONVERT(DATE, CAST(lc.DateFirstPurchase AS VARCHAR(20)), 103)
    FROM AdventureWorksLegacy.dbo.Customer$ AS lc
    JOIN AdventureWorksLegacy.dbo.SalesTerritory$ AS lst
        ON CAST(CAST(lc.SalesTerritoryKey AS VARCHAR(20)) AS INT) = CAST(CAST(lst.SalesTerritoryKey AS VARCHAR(20)) AS INT)
    ORDER BY CONCAT(CAST(lc.FirstName AS VARCHAR(50)), ' ', COALESCE( REPLACE( CAST(lc.MiddleName AS VARCHAR(50)), '.', ''), ''), ' ', CAST(lc.LastName AS VARCHAR(50)));
GO


/*
    Order/Sales
 */

-- Sales.Currency
INSERT INTO Sales.Currency (currencyCode, currencyName)
    SELECT DISTINCT CAST(lc.CurrencyAlternateKey AS CHAR(3)),
                    CAST(lc.CurrencyName AS VARCHAR(50)) AS cn
    FROM AdventureWorksLegacy.dbo.Currency$ AS lc
    ORDER BY cn;
GO

-- Sales.SalesOrder
-- Takes around 12 mins
INSERT INTO Sales.SalesOrder (salesOrderNumber, customerID, currencyID, salesTerritoryID, salesOrderDate, salesOrderShipDate, salesOrderDueDate)
    SELECT DISTINCT
        CAST(ls.SalesOrderNumber AS VARCHAR(20)) AS sonum,
        (SELECT customerID FROM AdventureWorks.Customers.Customer WHERE customerEmailAddress = CAST(lc.EmailAddress AS VARCHAR(50)) COLLATE Latin1_General_100_CI_AI_SC_UTF8),
        (SELECT currencyID FROM AdventureWorks.Sales.Currency WHERE currencyCode = CAST(lcc.CurrencyAlternateKey AS CHAR(3)) COLLATE Latin1_General_100_CI_AI_SC_UTF8),
        (SELECT salesTerritoryID FROM AdventureWorks.Localization.SalesTerritory WHERE salesTerritoryRegion = CAST(lst.SalesTerritoryRegion AS VARCHAR(50)) COLLATE Latin1_General_100_CI_AI_SC_UTF8),
        CONVERT(DATETIME2, CAST(ls.OrderDate AS VARCHAR(20)), 103), -- 103 is dd/MM/yyyy hh:mm to SQL Date (yyyy-MM-dd hh:mm)
        CONVERT(DATETIME2, CAST(ls.ShipDate AS VARCHAR(20)), 103),
        CONVERT(DATETIME2, CAST(ls.DueDate AS VARCHAR(20)), 103)
    FROM AdventureWorksLegacy.dbo.Sales$ AS ls
    JOIN AdventureWorksLegacy.dbo.Customer$ AS lc
        ON ls.CustomerKey = lc.CustomerKey
    JOIN AdventureWorksLegacy.dbo.Currency$ AS lcc
        ON ls.CurrencyKey = lcc.CurrencyKey
    JOIN AdventureWorksLegacy.dbo.SalesTerritory$ AS lst
        ON ls.SalesTerritoryKey = lst.SalesTerritoryKey
    ORDER BY sonum;
GO

-- Sales.SalesOrderLine - 5 mins
DECLARE @salesOrderLineNumber INT,
    @salesOrderID INT,
    @productID INT,
    @salesOrderLineQuantity INT,
    @salesOrderLineTaxAmt DECIMAL(16, 2),
    @salesOrderLineFreight DECIMAL(16, 2),
    @salesOrderLineUnitPrice DECIMAL(16, 2);

DECLARE salesOrderLineCursor CURSOR FOR
    SELECT
        CAST(ls.SalesOrderLineNumber AS INT) AS soln,
        (SELECT TOP 1 salesOrderID FROM AdventureWorks.Sales.SalesOrder WHERE salesOrderNumber = CAST(ls.SalesOrderNumber AS VARCHAR(20)) COLLATE Latin1_General_100_CI_AI_SC_UTF8) AS soid,
        (SELECT TOP 1 productID FROM AdventureWorks.Products.Product WHERE productName = CAST(lp.EnglishProductName AS VARCHAR(50)) COLLATE Latin1_General_100_CI_AI_SC_UTF8),
        CAST(ls.OrderQuantity AS INT),
        TRY_CAST( REPLACE( TRY_CAST(ls.TaxAmt AS VARCHAR(50)), ',', '.') AS DECIMAL(16, 2)),
        TRY_CAST( REPLACE( TRY_CAST(ls.Freight AS VARCHAR(50)), ',', '.') AS DECIMAL(16, 2)),
        TRY_CAST( REPLACE( TRY_CAST(ls.UnitPrice AS VARCHAR(50)), ',', '.') AS DECIMAL(16, 2))
    FROM AdventureWorksLegacy.dbo.Sales$ AS ls
    JOIN AdventureWorksLegacy.dbo.Products$ AS lp
        ON ls.ProductKey = lp.ProductKey
    ORDER BY soid, soln;

OPEN salesOrderLineCursor;
FETCH NEXT FROM salesOrderLineCursor INTO
    @salesOrderLineNumber, @salesOrderID, @productID, @salesOrderLineQuantity, @salesOrderLineTaxAmt, @salesOrderLineFreight, @salesOrderLineUnitPrice;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Insert Data
    INSERT INTO Sales.SalesOrderLine (salesOrderLineNumber, salesOrderID, productID, salesOrderLineQuantity, salesOrderLineTaxAmt, salesOrderLineFreight, salesOrderLineUnitPrice)
    SELECT @salesOrderLineNumber, @salesOrderID, @productID, @salesOrderLineQuantity, @salesOrderLineTaxAmt, @salesOrderLineFreight, @salesOrderLineUnitPrice;

    FETCH NEXT FROM salesOrderLineCursor INTO
        @salesOrderLineNumber, @salesOrderID, @productID, @salesOrderLineQuantity, @salesOrderLineTaxAmt, @salesOrderLineFreight, @salesOrderLineUnitPrice;
END

CLOSE salesOrderLineCursor;
DEALLOCATE salesOrderLineCursor;
GO