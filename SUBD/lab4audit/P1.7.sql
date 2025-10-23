Declare @a4 int = rand() * 1000
while @a4 % 5 = 0
set @a4 = @a4/5
if @a4 = 1 print 'Да'
else print 'Нет'