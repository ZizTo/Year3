Declare @a7 int = rand() * 5, 
@c7 int = 1
declare @b7 int = @a7 + rand() * 5
print '@a7=' + cast(@a7 as char(1)) +
', @b7=' + cast(@b7 as char(1))
while @a7 <= @b7 begin
print replicate(@a7, @c7)
set @a7 = @a7 + 1
set @c7 = @c7 + 1 end