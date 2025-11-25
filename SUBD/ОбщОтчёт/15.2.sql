use fastFood
select Pname from Products
where product_id in (
select product_id from Dish_Composition where dish_id in (
select dish_id from Dish where typeId = 1))
