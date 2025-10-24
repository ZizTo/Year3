use fastFood
select DName, 
abs(weight_g - (select avg(weight_g) from dish)) as diff
from Dish