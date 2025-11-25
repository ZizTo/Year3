use fastFood
go
Execute as user = 'MainUser'
Select top 1 * from Dish

begin transaction
Begin try Delete from Dish where dish_id = 17 end try
begin catch print 'Error' end catch
rollback transaction