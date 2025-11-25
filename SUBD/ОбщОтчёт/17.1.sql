use fastFood
declare @DName nVarchar(100) = 'Борщ'
if exists(select 1 from Dish where Dname = @DName) begin
print 'Блюдо ' + @DName + ' есть в меню' end
else print 'Блюдо ' + @DName + ' не найдено в меню'