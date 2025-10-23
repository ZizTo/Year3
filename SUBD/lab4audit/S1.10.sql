declare @n int = 1
while @n % 2 != 1 or @n % 3 != 1 
or @n % 4 != 1 or @n % 5 != 1 
or @n % 6 != 1 or @n % 7 != 0
set @n = @n + 1
print @n