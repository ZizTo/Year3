use lab1SUBD
select * from Tabl_Kontinent
where KolNas = (select Max(MinNas) from
(select Min(KolNas) as MinNas
from Tabl_Kontinent Group by Kontinent) A)