use fastFood
begin transaction
Declare @DishId int
Set @DishId = 231
exec addPreparation @DishId, 1000
if @DishId in (select dish_id from Dish) 
	commit transaction
else rollback transaction

select * from Preparations