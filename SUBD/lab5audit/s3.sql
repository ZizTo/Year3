use lab2SUBD
go
drop view if exists P3
go
create view P3(Фам, Должн, Зван, Степ, Каф, КолЭкз) 
as Select FIO, Dolgn, Zvanie, Stepen, NKaf, Count(O.Tab_Nom) from Sotrudnik C
join Prepodavatel P on C.TabNom = P.TabNom_Pr
join Kafedra K on C.ShifrKaf_Sotr = K.ShifrKaf
join Ozenka O on C.TabNom = O.Tab_Nom
group by FIO, Dolgn, Zvanie, Stepen, NKaf
go
Select * from P3