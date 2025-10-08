use fastFood
Select d.dish_id, d.DName, t.TName, d.technology, d.cook_time, d.weight_g, d.image_url
from Dish AS d
join DishType AS t on d.typeId = t.typeid