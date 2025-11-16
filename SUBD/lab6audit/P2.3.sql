Create function func3(@Nas as int, @Plo as float) returns float
as begin
Declare @P as float
Set @P = Round(cast(@Nas as float) / @Plo, 2)
return @P end
go
Select Nazvanie, KolNas, PL,
dbo.func3(KolNas, Pl) as pltn from Tabl_Kontinent