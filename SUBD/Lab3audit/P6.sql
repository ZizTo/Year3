use lab1SUBD
select * from Tabl_Kontinent where
Kontinent in (
Select Kontinent from Tabl_Kontinent
group by Kontinent Having
avg(KolNas) > (select avg(KolNas) from Tabl_Kontinent))