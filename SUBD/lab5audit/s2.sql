use lab1SUBD
go
create view P2 (Kontinent, PL, kolNas)
as select Kontinent, avg(pl), avg(KolNas) 
from Tabl_Kontinent group by Kontinent
go
select * from P2