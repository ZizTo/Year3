use fastFood
select Dname from Dish 
where cook_time < any (
select cook_time from Dish where typeId = 1)