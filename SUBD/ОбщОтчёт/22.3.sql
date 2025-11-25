use fastFood
Drop view if exists VVegetarian
go
Create view VVegetarian as 
Select DName, is_vegetarian
from Dish where is_vegetarian = 1
with check option
go
select * from VVegetarian
Delete from VVegetarian where DName = 'Гречка'
select * from Dish
