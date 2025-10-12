using DatabaseGrpcService;
using Google.Protobuf.WellKnownTypes;
using Grpc.Net.Client;

var builder = WebApplication.CreateBuilder(args);

// Регистрируем gRPC-клиент
builder.Services.AddGrpcClient<DatabaseManager.DatabaseManagerClient>(o =>
{
    // Адрес вашего gRPC-сервиса (убедитесь, что порт правильный)
    o.Address = new Uri("http://localhost:5001");
})
// ++ ИЗМЕНЕНИЕ ЗДЕСЬ ++
// Вместо GrpcWebHandler мы теперь конфигурируем HttpMessageHandler напрямую
.ConfigurePrimaryHttpMessageHandler(() =>
{
    return new HttpClientHandler
    {
        // Если ваш gRPC-сервис использует HTTPS с самоподписанным сертификатом (как в Visual Studio),
        // эта строка позволит клиенту ему доверять. Для HTTP она не нужна, но и не мешает.
        
    };
});


var app = builder.Build();

// --- API-эндпоинты, которые будет вызывать наш JavaScript ---

app.MapGet("/api/tables", async (DatabaseManager.DatabaseManagerClient client) =>
{
    var response = await client.GetTablesAsync(new Empty());
    return Results.Ok(response.TableNames);
});

app.MapPost("/api/data", async (InsertDataRequestBff request, DatabaseManager.DatabaseManagerClient client) =>
{
    var grpcRequest = new InsertDataRequest
    {
        TableName = request.TableName
    };
    foreach (var pair in request.Data)
    {
        // ++ ИЗМЕНЕНИЕ ЗДЕСЬ ++
        // Было: Value.ForObject(pair.Value)
        // Стало: ProtoConverter.ToValue(pair.Value)
        grpcRequest.Data.Add(pair.Key, ProtoConverter.ToValue(pair.Value));
    }
    await client.InsertDataAsync(grpcRequest);
    return Results.Ok(new { message = "Data inserted successfully." });
});

// ... (здесь можно добавить остальные эндпоинты для schema, data, create table) ...

app.UseDefaultFiles();
app.UseStaticFiles();

app.Run();

// Вспомогательный класс для приема JSON от клиента
public class InsertDataRequestBff
{
    public string TableName
    {
        get; set;
    }
    public Dictionary<string, object> Data
    {
        get; set;
    }
}