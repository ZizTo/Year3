use lab1SUBD
Select * from Tabl_Kontinent
where Kontinent = 'Àôðèêà' and
Exists (select * from Tabl_Kontinent 
where Kontinent='Àôðèêà' and KolNas > 100000000)