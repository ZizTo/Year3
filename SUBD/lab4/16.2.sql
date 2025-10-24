use fastFood
declare @AVGpr decimal(10,2)
select @AVGpr = avg(priceperkg) from Products
print 'avg price: ' + cast(@AVGpr as nvarchar(15))
select PName, priceperkg from Products
where priceperkg > @AVGpr
