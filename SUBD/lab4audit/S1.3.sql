use lab1SUBD
declare @l int = (select count(*) from Uch1)
if @l % 2 = 0 print 'chet'
else print 'nechet'