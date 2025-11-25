use fastFood
go
Create view VMenu as 
Select Dname as Название, weight_g as Вес, 
image_url as Картинка, is_vegetarian as Вегетарианское from Dish
go
select * from VMenu