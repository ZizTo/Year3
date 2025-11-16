use fastFood
go
Create trigger TR_VMenu_InsteadInsert on VMenu
instead of Insert as 
print 'Нельзя добавлять в представление меню, используйте таблицу'
go
Insert into VMenu(Название, Вес, Картинка, Вегетарианское) values
('Крутое блюдл', 100, 'Картинка', 1)