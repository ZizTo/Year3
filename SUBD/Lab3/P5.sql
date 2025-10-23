use lab1SUBD
select * from Tabl_Kontinent A
where KolNas > (select avg(kolnas) from Tabl_Kontinent B
where A.Kontinent = B.Kontinent)