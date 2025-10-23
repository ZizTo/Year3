declare @a6 int = 5, 
@b6 int = 10, @s int = 0
while @a6 <= @b6 begin
set @s = @s + @a6
set @a6 = @a6 + 1 end
print 's='+cast(@s as varchar(5))