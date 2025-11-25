use fastFood

select d.dname, p.pname, dc.weight_g
from Dish_Composition dc join dish d on d.dish_id = dc.dish_id
join Products p on dc.product_id = p.product_id
where d.DName = 'оливье'