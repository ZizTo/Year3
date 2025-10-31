use lab1SUBD
create table ##Pr7 (Nazv Varchar(50), Plotn float)
Insert into ##Pr7 (Nazv, Plotn)
select Nazvanie, Round(KolNas / Pl, 0) from Tabl_Kontinent
Select * from ##Pr7