use fastFood
go
Create view VType as 
Select d.DName as Название, dc.TName as Тип
from Dish d join DishType dc on d.typeId = dc.typeid
go
select * from VType