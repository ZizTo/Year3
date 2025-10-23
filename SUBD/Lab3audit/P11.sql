use lab1SUBD
select * from Tabl_Kontinent
where KolNas !> (select KolNas 
from Tabl_Kontinent where Nazvanie = 'Ангола')