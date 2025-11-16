create proc procM3 as begin 
drop table if exists TestTable 
create Table TestTable (CName NVarchar(50));
Insert into TestTable (CName)
Select Nazvanie from Tabl_Kontinent where Nazvanie like 'Ì%' end
go
execute procM3
Select * from TestTable