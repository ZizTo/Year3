drop proc if exists countProdInDish
go
create proc countProdInDish @DishName nvarchar(50), @Kol int output as
Select @kol = Count(dc.product_id) from Dish d
join Dish_Composition dc on d.dish_id = dc.dish_id
where d.DName = @DishName
go
Declare @k int
execute countProdInDish 'Цезарь', @k output
select @k