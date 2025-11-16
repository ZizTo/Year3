create function func2 (@Cont as Varchar(50))
returns varchar(50) as
begin
Declare @S as varchar(50) 
select @S = stolica from Tabl_Kontinent
where Nazvanie = @Cont return @S end
go
select dbo.func2('Австрия')