use lab1SUBD
Select * from Tabl_Kontinent
where Kontinent = 'Африка' and
KolNas > any (select KolNas from Tabl_Kontinent where Kontinent='Южная Америка')