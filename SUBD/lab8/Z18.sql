use fastFood
go
Create user testUser without login
go
grant insert on Dish to testUser
deny delete on Dish to testUser
revoke insert on Dish to testUser

drop user testUser