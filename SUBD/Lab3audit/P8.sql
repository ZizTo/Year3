use lab1SUBD
Select * from Tabl_Kontinent
where Kontinent = 'Европа' and
KolNas > any (select KolNas from Tabl_Kontinent where Kontinent='Южная Америка')