use lab1SUBD
select * from Tabl_Kontinent A
where PL > (select avg(PL) from Tabl_Kontinent B
where A.Kontinent = B.Kontinent)