use fastFood
select dish_id, DName from Dish where typeId = 5
union
select dish_id, DName from Dish where typeId = 6