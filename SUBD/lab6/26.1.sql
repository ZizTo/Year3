Drop function if exists fgetDishCost
go
Create function fgetDishCost(@DishId int)
returns float as begin
Declare @Cost float
select @Cost = cast(p.Цена as float) from Dish d 
join VPrice p on d.DName = p.название
where d.dish_id = @DishId
return @Cost end
go
Select DName, dbo.fgetDishCost(dish_id) as cost, weight_g from Dish