use fastFood
Create table Dish_Composition (
dish_id int,
product_id int,
weight_g Decimal(10,2) Not null,

Primary key (dish_id, product_id),
Foreign key (dish_id) References Dish(dish_id),
Foreign key (product_id) References Products(product_id) 
)