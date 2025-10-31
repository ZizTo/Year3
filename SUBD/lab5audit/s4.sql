Declare @P4 Table ([Номер месяца] INT, 
[Дата начала] Date, [Дата конца] Date)
Declare @N int = 1, @T date
set @T = cast(Year(getdate()) as Char(4)) + '0101'

while @N <= 12 begin
Insert into @P4 values
(@N, @T, EOMONTH(@T))
set @T = DATEADD(day, 1, EOMONTH(@T)) 
Set @N = @N + 1 end

select * from @P4