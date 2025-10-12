using DatabaseGrpcService.Services;

var builder = WebApplication.CreateBuilder(args);

// Добавляем CORS
builder.Services.AddCors(o => o.AddDefaultPolicy(builder =>
{
    // Разрешаем запросы от нашего будущего клиента
    builder.WithOrigins("http://localhost:7001", "https://localhost:7002") // Укажите порты вашего клиента
           .AllowAnyMethod()
           .AllowAnyHeader()
           .WithExposedHeaders("Grpc-Status", "Grpc-Message"); // Важно для gRPC-Web
}));

// Добавляем сервисы gRPC
builder.Services.AddGrpc();

var app = builder.Build();

// Включаем CORS
app.UseCors();

// Включаем gRPC-Web, чтобы браузер мог общаться с сервисом
app.UseGrpcWeb();

// Регистрируем наш сервис
app.MapGrpcService<DatabaseManagerService>().EnableGrpcWeb();

app.Run();
