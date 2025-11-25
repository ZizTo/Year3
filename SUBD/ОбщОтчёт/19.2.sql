use fastFood
select Dname, 
UPPER(left(DName, 3)+'-'+cast(dish_id as nvarchar(10))) as artic
from Dish