Create table ##DailySpec
(DishName Nvarchar(255), SpecPrice decimal(10,2))

Insert into ##DailySpec values ('Пюре', 0.15), ('Борщ', 2.2)

select vp.название, 
iif(ds.SpecPrice is not null, ds.SpecPrice, vp.Цена) from VPrice vp
left join ##DailySpec ds on vp.название = ds.DishName
