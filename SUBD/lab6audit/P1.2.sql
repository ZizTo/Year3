use lab1SUBD
go
Create proc Proc2 
@Буква as char(1), @Кол as Int output
as begin
select @Кол = COUNT(*) from Tabl_Kontinent 
where CHARINDEX(@Буква, Nazvanie) > 0 end
go 
Declare @K as Int
Declare @B as char(1)
Set @B = 'у'
Execute Proc2 @B, @K output
select @K as kol