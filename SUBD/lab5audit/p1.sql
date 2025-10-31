use lab1SUBD
go

Create view PR1
as select * from Tabl_Kontinent
where KolNas < 5000000 and pl > 100000
go

select * from PR1