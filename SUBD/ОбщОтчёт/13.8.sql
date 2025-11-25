use fastFood

select d.DName, p.PName
from Products p full outer join Dish_Composition dc 
on p.product_id = dc.product_id
left join Dish d on dc.dish_id = d.dish_id;


