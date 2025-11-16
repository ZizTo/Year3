use fastFood
go
Create Table ProdPriceChangeArch
(HistId int Primary key Identity, PName nvarchar(50), 
OldPrice decimal(10,2), NewPrice decimal(10,2))
go
create trigger TR_Products_AfterUpdate on Products
after update as begin
Insert into ProdPriceChangeArch(PName, OldPrice, NewPrice) 
select i.PName, d.priceperkg, i.priceperkg
from inserted i join deleted d on i.product_id = d.product_id
where i.priceperkg != d.priceperkg end
go
Update Products set priceperkg = 6.00 where PName = 'Спагетти'
select * from ProdPriceChangeArch