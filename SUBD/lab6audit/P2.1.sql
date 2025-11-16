Create function func1 (@a1 as float, @b1 as float)
returns Table as
return (select * from Tabl_Kontinent where PL between @a1 and @b1)
go
Select * from func1(100,10000	)