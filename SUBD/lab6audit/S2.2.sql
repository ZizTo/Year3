create function funcM2() returns table as
return (select Nazvanie, dbo.func3(KolNas, PL) as Pltn from Tabl_Kontinent)
go
Select * from funcM2()
