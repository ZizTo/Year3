select fio, case month(Data)
when 3 then 'весна'
when 4 then 'весна'
when 5 then 'весна'
when 6 then 'лето'
when 7 then 'лето'
when 8 then 'лето'
when 9 then 'осень'
when 10 then 'осень'
when 11 then 'осень'
else 'зима'
end as vrem_goda from Student