use lab1SUBD
Select * from Tabl_Kontinent
where Kontinent = 'Азия' and
KolNas > all (select KolNas from Tabl_Kontinent where Kontinent='Европа')