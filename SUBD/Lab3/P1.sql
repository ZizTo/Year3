use lab1SUBD
Select *, ROUND(CAST(kolNas as float) * 100 / 
(Select sum(kolnas) from Tabl_Kontinent), 3) as procen
from Tabl_Kontinent
order by procen desc