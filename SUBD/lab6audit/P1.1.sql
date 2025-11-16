Create proc Proc1 as 
begin select 
@@SERVERNAME as Serv, @@VERSION as Vers,
DB_NAME() as Bd, User as you, SYSTEM_USER as sys_you
end
	