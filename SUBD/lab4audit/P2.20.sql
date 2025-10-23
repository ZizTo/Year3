Select fio, data, case
when (month(data) = 3 and day(data) >= 21 or month(data)=4 and day(data)<=20) then 'овен'
when (month(data) = 4 and day(data) >= 21 or month(data)=5 and day(data)<=21) then 'телец'
when (month(data) = 5 and day(data) >= 22 or month(data)=6 and day(data)<=21) then 'близнец'
when (month(data) = 6 and day(data) >= 22 or month(data)=7 and day(data)<=22) then 'рак'
when (month(data) = 7 and day(data) >= 23 or month(data)=8 and day(data)<=21) then 'левв'
when (month(data) = 8 and day(data) >= 22 or month(data)=9 and day(data)<=23) then 'дева'
when (month(data) = 9 and day(data) >= 24 or month(data)=10 and day(data)<=23) then 'весы'
when (month(data) = 10 and day(data) >= 24 or month(data)=11 and day(data)<=22) then 'скорпион'
when (month(data) = 11 and day(data) >= 23 or month(data)=12 and day(data)<=22) then 'стрелец'
when (month(data) = 12 and day(data) >= 23 or month(data)=1 and day(data)<=20) then 'козерог'
when (month(data) = 1 and day(data) >= 21 or month(data)=2 and day(data)<=19) then 'водолей'
when (month(data) = 2 and day(data) >= 20 or month(data)=3 and day(data)<=20) then 'рыбы'
end as zodiak from Student