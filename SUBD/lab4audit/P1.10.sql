declare @n int = rand() * 10, 
@m int = 1, @s1 int = 0
while @m <= 2 * @n - 1 begin
set @s1 = @s1 + @m
print @s1
set @m = @m + 2 end