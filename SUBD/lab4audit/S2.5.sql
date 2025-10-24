use lab1SUBD
select Left(FIO, CHARINDEX(' ', FIO) - 1) +
SUBSTRING(fio, CHARINDEX(' ', FIO) + 1, 1) +
SUBSTRING(fio, CHARINDEX(' ', FIO, CHARINDEX(' ', FIO) + 1) + 1, 1) 
from Student