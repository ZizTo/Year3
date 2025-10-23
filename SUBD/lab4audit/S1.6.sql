declare @a int = rand() * 1000
print @a
if @a % 11 = 0 print 'yeah'
else print 'no'