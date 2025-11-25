use lab2SUBD

select s.Fio_stud
from Student s join Ozenka o on s.Reg_Nom = o.RegNom
where o.data != '2022-06-05'