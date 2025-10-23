Declare @a5 int = rand() * 1000,
@b5 int = rand() * 1000
print '@a5=' + cast(@a5 as varchar(4))
print '@b5=' + cast(@b5 as varchar(4))

while @a5 != @b5
begin if @a5 > @b5
set @a5 = @a5 - @b5
else set @b5 = @b5 - @a5 end
print 'мнд=' + cast(@a5 as varchar(4))