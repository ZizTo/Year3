use fastFood
Drop view if exists VGarnirs
go
Create view VGarnirs as 
Select DName, weight_g, image_url, cook_time, technology, is_vegetarian, typeId
from Dish where typeId = 1
with check option
go
select * from VGarnirs
Insert into VGarnirs values ('Гречка', 200, 'photo.link', 10, 'easy', 1, 1)
select * from Dish
go 
Insert into VGarnirs values ('Гречка', 200, 'photo.link', 10, 'easy', 1, 2)