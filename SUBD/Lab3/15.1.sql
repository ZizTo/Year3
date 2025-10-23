use fastFood
select * from Dish
where cook_time > (Select avg(cook_time) from Dish)