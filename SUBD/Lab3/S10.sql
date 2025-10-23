use lab1SUBD
select * from Tabl_Kontinent
where Kontinent = (select Kontinent 
from Tabl_Kontinent where Nazvanie = 'Бангладеш')