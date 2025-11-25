use fastFood
declare @Nacenka decimal(10,2) = 1.35

select d.DName,
sum(dc.weight_g * p.priceperkg / 1000) as 'себест',
sum(dc.weight_g * p.priceperkg / 1000) * @Nacenka as 'без окр',
Ceiling(sum(dc.weight_g * p.priceperkg / 1000) * @Nacenka) as 'На продажу',
Floor(sum(dc.weight_g * p.priceperkg / 1000) * @Nacenka) as 'Если акция'

from Dish d 
join Dish_Composition dc on d.dish_id = dc.dish_id
join Products p on dc.product_id = p.product_id
group by d.DName