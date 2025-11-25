use fastFood
Drop view if exists VProductsPrice
go
Create view VProductsPrice as 
Select PName, priceperkg
from Products
with check option
go
select * from VProductsPrice
Update VProductsPrice set priceperkg = 1.45 where PName = 'Картофель'
select * from VProductsPrice
