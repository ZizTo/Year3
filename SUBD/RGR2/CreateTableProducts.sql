use fastFood
Create table Products (
product_id int identity primary key,
PName Nvarchar(255) Not null,
caloriesperg Decimal(10,2) Not null,
priceperkg Decimal(10,2) Not null,
)