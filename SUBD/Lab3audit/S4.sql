use lab1SUBD
Select *, ROUND(CAST(PL as float) * 100 / (
Select sum(PL) from Tabl_Kontinent B where A.Kontinent = B.Kontinent),
3) as procen
from Tabl_Kontinent A
order by procen desc