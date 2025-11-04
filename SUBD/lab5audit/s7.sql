use lab1SUBD
create table ##P7 (Nazv Varchar(50), plmin int, plmax int)
Insert into ##P7 (Nazv, plmin, plmax)
select Kontinent, Min(pl), Max(pl) from Tabl_Kontinent
group by Kontinent
Select * from ##P7