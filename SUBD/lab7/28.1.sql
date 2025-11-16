use fastFood
go
create trigger TR_Dish_AfterInsert on Dish after insert
as begin 
declare @DishName nvarchar(50)
select @DishName = DName from inserted
print @DishName + ' добавлено' end
go
Insert into Dish(DName, typeId, weight_g, 
image_url, cook_time, technology, is_vegetarian) values
('Гречка', 1, 100, 'Картинка', 12, 'Сварить в каструле', 1)