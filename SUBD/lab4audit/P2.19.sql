select fio, data, spez, godpost,
iif(godpost - year(data) <= 18, 
'молодой','старше') as vozr
from Student