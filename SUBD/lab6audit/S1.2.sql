create proc procM2 @inp int, @dig int output
as set @dig = len(cast(abs(@inp) as varchar(50)));
go
Declare @res int
execute procM2 2131, @res output
select @res as kol 