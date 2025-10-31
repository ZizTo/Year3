use lab1SUBD
go
create view PR2 (Kontinent, PL, kolNas)
as select Kontinent, sum(pl), sum(KolNas) 
from Tabl_Kontinent group by Kontinent
go
select * from PR2