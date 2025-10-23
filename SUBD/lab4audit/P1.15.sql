declare @l2 int, @m2 int,
@n2 char(13)
set @n2 = 'Нижневратовск'
set @l2 = len(@n2) set @m2 = @l2
while @l2 > 0 begin
print 
left(@n2, @l2) + space (2*(@m2 - @l2)) 
+ right(reverse(@n2), @l2) 
set @l2=@l2-1 end
set @l2 = 2
while @l2 <= @m2 begin
print 
left(@n2, @l2) + space (2*(@m2 - @l2)) 
+ right(reverse(@n2), @l2) 
set @l2=@l2+1 end