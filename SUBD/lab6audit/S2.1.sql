Create function funcM1 (@a1 as float)
returns Table as
return (select * from Tabl_Kontinent where PL < @a1)
go
Select * from funcM1(100000)