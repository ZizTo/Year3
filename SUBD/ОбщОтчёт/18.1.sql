use fastFood
select datename(weekday, PrepDate), 
sum(countDish) as kol from Preparations
group by datename(weekday, PrepDate)
order by kol Desc