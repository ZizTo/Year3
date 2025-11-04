use lab1SUBD
declare @P5 Table (Название varchar(50), 
Столица Varchar(50), Площадь bigint, 
Население int, Континент Varchar(50))

Insert into @P5 Select Nazvanie, 
Stolica, PL, KolNas, Kontinent from Tabl_Kontinent K1
where cast(Pl as bigint) * 100 < 
(select avg(PL) from Tabl_Kontinent K2 where K1.Kontinent = K2.Kontinent)

select * from @P5