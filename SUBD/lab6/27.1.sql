create function fGetIngredients(@DishId int) returns table as
return (select p.PName, dc.weight_g from Dish_Composition dc
join Products p on dc.product_id = p.product_id
where dc.dish_id = @DishId)
go
select * from fGetIngredients(16)