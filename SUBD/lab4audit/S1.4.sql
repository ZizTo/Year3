declare @n int = rand() * 1000, @s int = 0
print @n
while @n % 10 != 0 begin
set @s = @s + @n % 10
set @n = @n / 10 end
print @s