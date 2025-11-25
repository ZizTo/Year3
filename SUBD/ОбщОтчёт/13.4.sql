use fastFood

select p.product_id, p.PName, d.DName
from Dish d join Dish_Composition dc on dc.dish_id = d.dish_id
right join Products p on p.product_id = dc.product_id
