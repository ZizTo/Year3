use lab1SUBD
go
Create trigger trig3 on Student
for delete as begin
Insert StudArch select 
FIO, Data, spez, godpost, getdate() as DelOn
from deleted end