use fastFood
Select * from Dish d
where exists (select 'more' from Dish_Composition dc
join Products p on dc.product_id=p.product_id
where dc.dish_id = d.dish_id and p.priceperkg > 20)