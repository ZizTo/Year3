use fastFood
go
Create table DishArch(arch_id int Primary key identity,
DName nvarchar(50), typeId int, delDate Date)
go
Create trigger TR_Dish_AfterDelete on Dish after delete as begin
Insert into DishArch(DName, typeId, delDate) select
DName, typeId, getdate() as delDate from deleted end
go
Delete from dish where DName = 'Гречка'
Select * from DishArch