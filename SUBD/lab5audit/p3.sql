use lab2SUBD
go
create view PR3(Фам, Должн, Зван, Степ, Каф, Зарпл) 
as Select FIO, Dolgn, Zvanie, Stepen, NKaf, Zarplata from Sotrudnik C
join Prepodavatel P on C.TabNom = P.TabNom_Pr
join Kafedra K on C.ShifrKaf_Sotr = K.ShifrKaf
go
Select * from PR3