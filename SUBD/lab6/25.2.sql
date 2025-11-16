use fastFood
drop proc if exists getPrice
go
create proc getPrice @DishId Nvarchar(50), @Pri float output as
Select @Pri = cast(Цена as float) from VPrice
where название = @DishId
go
Declare @Pri float
execute getPrice 'Цезарь', @Pri output
select @Pri

 