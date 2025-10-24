use fastFood
begin try
insert into Dish(Dname) values
('Крутое блюдо')
print 'Запись добалвена'
end try
begin catch
print 'ошибка при добавлении ' + error_message()
end catch	