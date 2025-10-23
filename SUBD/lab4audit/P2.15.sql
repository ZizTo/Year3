select getdate() 
select day(getdate()),
month(getdate()), year(getdate()),
datepart(hour, getdate()),
datepart(second, getdate())