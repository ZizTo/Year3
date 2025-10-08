use fastFood
Create table Dish (
dish_id int identity primary key,
DName Nvarchar(255) Not null,
typeId int Not null,
weight_g int not null,
image_url Nvarchar(255) Not null,
cook_time int not null,
technology Nvarchar(255) Not null,

Foreign key (typeId) References DishType(typeid) 
)