Declare @l int, 
@n1 char(13) = 'Матиевский'

set @l = len(@n1)
while @l > 0 begin
print @n1 
set @l = @l - 1 end