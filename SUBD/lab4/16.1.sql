use fastFood
declare @DType nvarchar(100)
Set @DType = 'Гарнир'

select * from Dish 
where typeId = (select typeid from DishType where @DType = TName)