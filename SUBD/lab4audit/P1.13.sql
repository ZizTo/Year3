declare @d int = 31, @m int = 12
set @d = case 
when @m in (1,3,5,7,8,10,12) and @d = 31
then 1
when @m in (4,6,9,11) and @d = 30
then 1
when @m = 2 and @d = 29 then 1
else @d + 1 end

set @m = case
when @d = 1 and @m = 12 then 1
when @d = 1 and @m < 12 then @m + 1
else @m end

print cast(@d as varchar(2)) + '/' 
+ cast(@m as varchar(2))