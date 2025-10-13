using DatabaseGrpcService.Services;

var builder = WebApplication.CreateBuilder(args);

// Добавляем CORS
builder.Services.AddCors(o => o.AddDefaultPolicy(builder =>
{
    // Разрешаем запросы от нашего будущего клиента
    builder.WithOrigins("http://localhost:5202") // Укажите порты вашего клиента
           .AllowAnyMethod()
           .AllowAnyHeader()
           .WithExposedHeaders("Grpc-Status", "Grpc-Message"); // Важно для gRPC-Web
}));

// Добавляем сервисы gRPC
builder.Services.AddGrpc();

var app = builder.Build();

app.UseCors();

// ++ ЭТА СТРОКА ТЕПЕРЬ БУДЕТ РАБОТАТЬ ++
// Включает middleware, который понимает запросы gRPC-Web
app.UseGrpcWeb();

// Регистрируем наш сервис...
app.MapGrpcService<DatabaseManagerService>()
   // ++ ...И ЯВНО РАЗРЕШАЕМ ДЛЯ НЕГО gRPC-Web ++
   .EnableGrpcWeb();

app.Run();
