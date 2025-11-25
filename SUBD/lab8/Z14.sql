use fastFood
go
Create schema Production authorization DishReaderRole
go
create table Production.Orders(OrderID int primary key, OrderDate date);
go
create view Production.V_Orders as select * from Production.Orders;