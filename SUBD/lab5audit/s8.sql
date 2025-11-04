use lab2SUBD;
with C3K as (select K.NKaf as kaf, K.ShifrKaf, avg(Zarplata) as [sr po kaf]
from Sotrudnik C join Kafedra K on C.ShifrKaf_Sotr = K.ShifrKaf
Group by K.NKaf, K.ShifrKaf)
select C.Fio, C.Zarplata, CK.kaf, Ck.[sr po kaf]
from Sotrudnik C join C3K CK on C.ShifrKaf_Sotr = CK.ShifrKaf 
where C.Zarplata < CK.[sr po kaf]