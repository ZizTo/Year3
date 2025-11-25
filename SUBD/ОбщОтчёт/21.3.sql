use fastFood
Drop view if exists VPrice
go
Create view VPrice as Select d.DName as название, 
Sum(p.priceperkg / 1000 * dc.weight_g ) as Цена from Dish d 
join Dish_Composition dc on d.dish_id = dc.dish_id
join Products p on dc.product_id = p.product_id
group by d.DName
go
select * from VPrice