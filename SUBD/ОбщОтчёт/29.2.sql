use fastFood
go
Create trigger TR_Preparations_InsteadUpdate on Preparations
instead of Update as
print 'Не изменяйте приготвления, это может помешать анализу'
go
Update Preparations set countDish=1000 where prep_id = 1
select * from Preparations