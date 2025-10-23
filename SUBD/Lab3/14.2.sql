use fastFood
select dish_id, DName from Dish where is_vegetarian = 1
intersect 
select dish_id, DName from Dish where typeId = 1