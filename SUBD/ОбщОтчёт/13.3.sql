use fastFood

select p.product_id, p.PName, d.DName
from Products p left join Dish_Composition dc on p.product_id = dc.product_id
left join Dish d on dc.dish_id = d.dish_id