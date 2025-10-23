Declare @a4 int = rand() * 100
print @a4
while @a4 % 3 = 0
set @a4 = @a4/3
if @a4 = 1 print 'Да'
else print 'Нет'