use lab1SUBD
Select * from Tabl_Kontinent
where Kontinent = 'Южная Америка' and
KolNas > all (select KolNas from Tabl_Kontinent where Kontinent='Африка')