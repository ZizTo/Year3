use lab1SUBD
select * from Tabl_Kontinent
where PL > (select avg(PL) from Tabl_Kontinent)