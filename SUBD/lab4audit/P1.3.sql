use lab1SUBD
declare @maxp float, @minp float, 
@diff float

select @maxp = Max(ball),
@minp = min(ball) from Uch1

set @diff = @maxp - @minp
print @diff