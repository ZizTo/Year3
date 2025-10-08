Create database fastFood on (
NAME = fastFood_data,
FILENAME = 'C:\SQLTESTDATA\fastfood_data.mdf',
SIZE = 10MB,
MAXSIZE = 100MB,
FILEGROWTH = 1MB
)
LOG on (
NAME = fastFood_log,
FILENAME = 'C:\SQLTESTDATA\fastfood_log.ldf',
SIZE = 10MB,
MAXSIZE = 100MB,
FILEGROWTH = 1MB
)