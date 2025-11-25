Declare @GarnirsIds Table (id int)

insert into @GarnirsIds 
select dish_id from Dish where typeId = 1

select p.PName from Products p 
join Dish_Composition dc on p.product_id = dc.product_id
where dc.dish_id in (Select id from @GarnirsIds)