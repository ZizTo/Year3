use lab1SUBD
go
drop view if exists P1
go
Create view P1 as select Nazvanie, KolNas, Pl from Tabl_Kontinent
where Kontinent = 'Àôðèêà' and KolNas > 10000 and Pl > 50000
go
select * from P1