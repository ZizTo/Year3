declare @a8 int = 10
while @a8 < 100 begin
if (@a8 % 4 = 0) and 
(@a8 % 6 != 0) print @a8
set @a8 = @a8 + 1 end