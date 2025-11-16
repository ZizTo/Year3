create proc addPreparation @DishId int, @Portions int as
Insert into Preparations (dish_id, countDish, PrepDate) values
(@DishId, @Portions, GETDATE())
go
execute addPreparation 16, 10
select * from Preparations