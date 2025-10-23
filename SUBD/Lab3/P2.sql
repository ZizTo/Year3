use lab1SUBD
select * from Tabl_Kontinent
where KolNas > (select avg(kolnas) from Tabl_Kontinent)