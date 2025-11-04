use fastFood
Drop view if exists VProcents
go
Create view VProcents as Select d.DName as название, 
	cast(sum(p.countDish) as float) / 
	(select sum(CountDish) from Preparations) * 100 
as Процент
from Dish d join Preparations p on d.dish_id = p.dish_id
group by d.DName
go
select * from VProcents order by Процент desc