Declare @a1 int, @b1 int
set @a1 = rand()*1000
set @b1 = square(@a1)
print @a1 
print @b1