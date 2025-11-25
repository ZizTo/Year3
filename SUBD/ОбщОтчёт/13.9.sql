use fastFood

select f.pname, f.caloriesperg, s.caloriesperg, s.PName 
from Products f cross join Products s
where (f.caloriesperg - s.caloriesperg between -1.0 and 1.0)
and f.PName != s.PName

