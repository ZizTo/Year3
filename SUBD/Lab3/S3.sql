use lab1SUBD
Select * from
(select * from Tabl_Kontinent
where Kontinent='Европа') A
where KolNas < 5000000