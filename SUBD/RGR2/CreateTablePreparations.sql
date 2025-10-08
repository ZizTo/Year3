use fastFood
Create table Preparations (
prep_id int identity primary key,
dish_id int Not null,
countDish int Not null,
PrepDate date not null,

Foreign key (dish_id) References Dish(dish_id) 
)