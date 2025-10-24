use lab1SUBD
select distinct spez, 
iif(len(spez) > 7, 'dlin', 'korotk')
from Student