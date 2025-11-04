use lab2SUBD
Drop Table if exists #P6
select Datepart(WEEKDAY, data) as [Nom ned],
Count(Distinct kod) as [kol exam],
count(Distinct RegNom) as [kol stud] into #P6
from Ozenka group by Datepart(WEEKDAY, data)
select * from #P6