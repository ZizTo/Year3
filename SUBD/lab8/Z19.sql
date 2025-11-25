use fastFood
go
create user Admin1 without login
create user Admin2 without login
go
alter role db_owner add member Admin1
alter role db_owner add member Admin2
go
Deny create table to Admin2
go
print 'Admin 1'
execute as user = 'Admin1'
Create table TestTbl1(id int); Drop table TestTbl1
revert
print 'Admin 2'
execute as user = 'Admin2'
begin try 
Create table TestTbl2(id int); Drop table TestTbl2
end try
begin catch print 'Error' end catch
drop user Admin1
drop user Admin2