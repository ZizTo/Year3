use fastFood
select PName from Products where product_id in
(select product_id from Dish_Composition where 
dish_id = (Select dish_id from Dish where DName = 'Паста карбонара')
except
select product_id from Dish_Composition where 
dish_id = (Select dish_id from Dish where DName = 'Макароны с ветчиной и сыром'))