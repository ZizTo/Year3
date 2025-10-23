use lab1SUBD
Select *, ROUND(CAST(PL as float) * 100 / 
(Select sum(PL) from Tabl_Kontinent), 3) as procen
from Tabl_Kontinent
order by procen desc