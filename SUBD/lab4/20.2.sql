use fastFood
select p.PName, sum(dc.weight_g) / 100,
Ceiling(sum(dc.weight_g) / 100) as upak
from Dish_Composition dc
join Products p on dc.product_id = p.product_id
group by p.PName