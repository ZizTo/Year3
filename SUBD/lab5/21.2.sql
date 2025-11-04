use fastFood
go
Create view Vkal as Select d.DName as название, 
Sum(p.caloriesperg * dc.weight_g) as Каллорийность from Dish d 
join Dish_Composition dc on d.dish_id = dc.dish_id
join Products p on dc.product_id = p.product_id
group by d.DName
go
select * from VKal