Drop function if exists fhowPopular
go
Create function fhowPopular(@DishId int) returns nvarchar(50) as begin
Declare @TotaProcent Float 
Select @TotaProcent = 
	cast (Sum(countDish) as float) / (select Sum(countDish) from Preparations)
from Preparations where dish_id = @DishId
If @TotaProcent > 0.15 return 'Хит'
if @TotaProcent > 0.1 return 'Очень популярное'
if @TotaProcent > 0.05 return 'Популярное'
if @TotaProcent > 0 return 'Непопулярное'
return 'Ни разу не брали' end
go 
Select DName, dbo.fhowPopular(dish_id) as pop from Dish