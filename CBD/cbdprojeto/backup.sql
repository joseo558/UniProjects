/*
    FILE 7
    Commands to backup the database

    PascalCase for table names
    camelCase for column names
    Type_Snake_Case for constraints, functions, procedures, triggers, views, etc.

    @author: Jose Oliveira; Diogo Oliveira
 */
USE AdventureWorks;
GO

-- EXEC sys.xp_create_subdir N'/var/opt/mssql/data/BackupAW'; -- for docker
EXEC sys.xp_create_subdir N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\BackupAW';
GO

-- Integral backup (for testing, use external storage)
BACKUP DATABASE AdventureWorks TO DISK = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\BackupAW\FullAW1.bak';
GO

-- Differential backup (for testing, use external storage)
BACKUP DATABASE AdventureWorks TO DISK = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\BackupAW\DiffAW1.bak' WITH DIFFERENTIAL;
GO

-- Log backup (for testing, use external storage)
BACKUP LOG AdventureWorks TO DISK = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\BackupAW\LogAW1.bak';
GO

-- For restoring
USE master;
-- Backup tail of the log (for testing, use external storage)
BACKUP LOG AdventureWorks TO DISK = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\BackupAW\LogAWTail.bak' WITH NORECOVERY, NO_TRUNCATE;
GO
-- Restore database (for testing, use external storage)
RESTORE DATABASE AdventureWorks FROM DISK = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\BackupAW\FullAW1.bak' WITH NORECOVERY;
RESTORE DATABASE AdventureWorks FROM DISK = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\BackupAW\DiffAW1.bak' WITH NORECOVERY;
RESTORE LOG AdventureWorks FROM DISK = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\BackupAW\LogAW1.bak' WITH NORECOVERY;
RESTORE LOG AdventureWorks FROM DISK = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\BackupAW\LogAWTail.bak' WITH RECOVERY;