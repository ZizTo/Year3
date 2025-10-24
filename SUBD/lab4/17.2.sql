use fastFood
declare @kol int = 0, @c int, @cmax int
Select @c = min(dish_id), @cmax = max(dish_id) from Dish

while @c <= @cmax begin
declare @price Decimal(10,5)
Select @price = sum(p.priceperkg * dc.weight_g / 1000) from Dish d 
join Dish_Composition dc on d.dish_id = dc.dish_id
join Products p on dc.product_id = p.product_id
where d.dish_id = @c
if @price > 5 set @kol = @kol + 1
set @c = @c + 1 end

print 'Kol dorogih blud: ' + cast(@kol as nvarchar(10))