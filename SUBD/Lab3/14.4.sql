use fastFood
select dish_id, DName from Dish 
except 
select dish_id, DName from Dish where typeId = 1