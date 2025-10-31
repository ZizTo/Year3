use lab2SUBD
select DATENAME(Month, data) as [Nazv mes],
Count(Distinct kod) as [kol exam],
count(Distinct RegNom) as [kol stud] into #PR6
from Ozenka group by DATENAME(month, data)
select * from #PR6