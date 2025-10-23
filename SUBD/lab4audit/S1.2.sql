use lab1SUBD
Declare @licey float, @gimn float, @diff float

set @licey = (select avg(ball) from Uch1 where ush='Лицей')

set @gimn = (select avg(ball) from Uch1 where ush='Гимназия')

set @diff = abs(@licey - @gimn) print @diff