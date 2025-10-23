use lab1SUBD
Select * from Tabl_Kontinent
where Kontinent = 'Àôðèêà' and
Exists (select * from Tabl_Kontinent 
where Kontinent='Àôðèêà' and PL > 2000000)