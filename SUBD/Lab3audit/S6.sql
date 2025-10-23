use lab1SUBD
select * from Tabl_Kontinent where
Kontinent in (
Select Kontinent from Tabl_Kontinent
group by Kontinent Having
avg(PL) > (select avg(PL) from Tabl_Kontinent))