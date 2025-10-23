use lab1SUBD

declare @a3 int = rand() * 100
if @a3 % 3 = 0
print cast(@a3 as varchar(3)) + 
' делится на 3'
else 
print cast(@a3 as varchar(3)) + 
' не делится на 3'