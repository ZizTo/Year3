use fastFood
Drop view if exists VDishInfo
go
Create view VDishInfo as
Select k.название, k.Каллорийность, p.Цена from Vkal k
join VPrice p on k.название = p.название
go 
Select * from VDishInfo