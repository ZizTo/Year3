create proc proc3
@Конт as varchar(50) = 'Европа' as 
begin select top 3 * from Tabl_Kontinent
where Kontinent = @Конт order by PL end
go 
execute proc3 Default