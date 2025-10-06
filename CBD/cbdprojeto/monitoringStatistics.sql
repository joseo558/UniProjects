/*
    FILE 5
    Create procedures for monitoring the database

    PascalCase for table names
    camelCase for column names
    Type_Snake_Case for constraints, functions, procedures, triggers, views, etc.

    @author: Jose Oliveira; Diogo Oliveira
 */

USE AdventureWorks;
GO

DROP PROCEDURE IF EXISTS Monitoring.sp_Monitoring_Statistics;
GO
CREATE PROCEDURE Monitoring.sp_Monitoring_Statistics
AS
BEGIN
    INSERT INTO Monitoring.DBStatistics (dbStatisticsTable, dbStatisticsNumberRows, dbStatisticsSpaceKB, dbStatisticsSpaceReservedKB)
    SELECT t.name,
           ps.row_count,
           SUM(ps.used_page_count) * 8, -- a page is 8 KB
           SUM(ps.reserved_page_count) * 8
    FROM sys.tables t
    INNER JOIN sys.dm_db_partition_stats ps ON t.object_id = ps.object_id
    GROUP BY t.name, ps.row_count
    ORDER BY t.name
END;
GO

-- View to see the latest statistics
DROP VIEW IF EXISTS Monitoring.vw_Latest_Statistics;
GO
CREATE VIEW Monitoring.vw_Latest_Statistics
AS
SELECT dbStatisticsTable, dbStatisticsNumberRows, dbStatisticsSpaceKB, dbStatisticsSpaceReservedKB
FROM Monitoring.DBStatistics
WHERE dbStatisticsDate = (SELECT MAX(dbStatisticsDate) FROM Monitoring.DBStatistics);
GO

-- Execute the procedure
EXEC Monitoring.sp_Monitoring_Statistics;
GO

-- View the latest statistics
SELECT * FROM Monitoring.vw_Latest_Statistics;
GO