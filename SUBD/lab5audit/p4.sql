Declare @PR4 Table ([Номер недели] INT, 
[Дата начала] Date, [Дата конца] Date)
Declare @T as Date, @N int = 1
set @T = cast(Year(getdate()) as Char(4)) + '0101'
while Datepart(weekday, @T) > 1 Set @T = Dateadd(Day, -1, @T)
Print Datepart(week, @T)
while Year(@T) < Year(Dateadd(Year, 1, getdate())) begin
insert @PR4 values (@N, @T, Dateadd(Day, 6, @T))
set @T = dateadd(day, 7, @T)
set @N = @N + 1 end

select * from @PR4