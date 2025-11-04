use fastFood;

with DishCost as (
Select d.DName, d.typeId as typeId, 
Sum(p.priceperkg / 1000 * dc.weight_g ) as Cost from Dish d 
join Dish_Composition dc on d.dish_id = dc.dish_id
join Products p on dc.product_id = p.product_id
group by d.DName, d.typeId
), TypeAvgCost as (
Select typeId, Avg(Cost) as AvgCost from DishCost group by typeId
)
Select dc.DName, dc.Cost, ta.AvgCost, dc.typeId 
from DishCost dc join TypeAvgCost ta on dc.typeId = ta.typeId