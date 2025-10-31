use lab1SUBD
declare @PR5 Table (Название varchar(50), 
Столица Varchar(50), Площадь bigint, 
Население int, Континент Varchar(50))

Insert into @PR5 Select Nazvanie, 
Stolica, PL, KolNas, Kontinent from Tabl_Kontinent
where cast(Pl as bigint) * 1000 < (select avg(PL) from Tabl_Kontinent)

select * from @PR5