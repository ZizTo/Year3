use lab1SUBD
Declare @a2 Int = rand()*100,
@b2 int = rand() * 100

if @a2 > @b2 
print '@a2 = ' + cast(@a2 as varchar(3))
else
print '@b2 = ' + cast(@b2 as varchar(3))