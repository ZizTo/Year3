use fastFood
go
create proc GetDishIngr @DishName Varchar(50) as begin
Select p.PName, dc.weight_g from Dish d 
join Dish_Composition dc on d.dish_id = dc.dish_id
join Products p on dc.product_id = p.product_id
where d.DName = @DishName end
go
exec GetDishIngr 'Цезарь'