declare @a int = rand() * 100,
@b int = rand() * 100, @c int = rand() * 100
print @a print @b print @c
if @a < @b set @a = @b
if @a < @c set @a = @c
print @a