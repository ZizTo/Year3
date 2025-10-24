use fastFood
select min(abs(DATEDIFF(day, getdate(), PrepDate)))
from Preparations