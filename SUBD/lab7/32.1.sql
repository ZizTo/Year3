use fastFood

Begin transaction 
begin try
Insert into Dish(DName, typeId, weight_g, 
image_url, cook_time, technology, is_vegetarian) values
('Гречка', 1, 100, 'Картинка', 12, 'Сварить в каструле', 1)
insert into Products(PName, caloriesperg) values ('Гречка', 1000)
commit transaction
print 'Всё хорошо'
end try
begin catch
rollback transaction
print 'Отменяем, ошибка: ' + Error_Message()
end catch

Select * from Dish