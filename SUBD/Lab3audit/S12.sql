use lab1SUBD
select * from Tabl_Kontinent
where KolNas = (select max(KolNas) from Tabl_Kontinent
where PL in (select Min(PL) as MinNas
from Tabl_Kontinent Group by Kontinent)
)