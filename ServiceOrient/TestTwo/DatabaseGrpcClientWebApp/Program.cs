using DatabaseGrpcService;
using Google.Protobuf.WellKnownTypes;
using Grpc.Net.Client;
using DatabaseGrpcService.Services;
using Grpc.Net.Client.Web;

var builder = WebApplication.CreateBuilder(args);

// Регистрируем gRPC-клиент
builder.Services.AddGrpcClient<DatabaseManager.DatabaseManagerClient>(o =>
{
    // Адрес вашего gRPC-сервиса (убедитесь, что порт правильный)
    o.Address = new Uri("http://localhost:5074");
})
// ++ ИЗМЕНЕНИЕ ЗДЕСЬ ++
// Вместо GrpcWebHandler мы теперь конфигурируем HttpMessageHandler напрямую
.ConfigurePrimaryHttpMessageHandler(() =>
 {
     // ++ ЭТО И ЕСТЬ РЕШЕНИЕ ++
     // Мы явно создаем GrpcWebHandler. Он будет "переводить"
     // стандартные gRPC-вызовы в формат gRPC-Web, понятный серверу.
     return new GrpcWebHandler(new HttpClientHandler());
 });

builder.Services.AddGrpc(options =>
{
    options.EnableDetailedErrors = true;
});

var app = builder.Build();

// --- API-эндпоинты, которые будет вызывать наш JavaScript ---

app.MapGet("/api/tables", async (DatabaseManager.DatabaseManagerClient client) =>
{
    var response = await client.GetTablesAsync(new Empty());
    return Results.Ok(response.TableNames);
});

// ++ ДОБАВЛЯЕМ GET /api/tables/{tableName}/schema ++
app.MapGet("/api/tables/{tableName}/schema", async (string tableName, DatabaseManager.DatabaseManagerClient client) =>
{
    var request = new GetTableSchemaRequest { TableName = tableName };
    var response = await client.GetTableSchemaAsync(request);
    // Отправляем клиенту список колонок в виде JSON
    return Results.Ok(response.Columns);
});

// ++ ДОБАВЛЯЕМ GET /api/tables/{tableName}/data ++
app.MapGet("/api/tables/{tableName}/data", async (string tableName, DatabaseManager.DatabaseManagerClient client) =>
{
    var request = new GetTableDataRequest { TableName = tableName };
    var response = await client.GetTableDataAsync(request);
    // Преобразуем Protobuf Struct в более удобный для JSON Dictionary
    var result = response.Rows.Select(row =>
        row.Fields.ToDictionary(
            f => f.Key,
            f => ProtoConverter.ToObject(f.Value) // <-- ИСПОЛЬЗУЕМ НАШ МЕТОД
        )
    );
    return Results.Ok(result);
});


// ++ ДОБАВЛЯЕМ POST /api/tables ++
app.MapPost("/api/tables", async (CreateTableRequestBff request, DatabaseManager.DatabaseManagerClient client) =>
{
    var grpcRequest = new TableDefinition { TableName = request.TableName };
    // Преобразуем (мапим) объекты из HTTP-запроса в объекты для gRPC-запроса
    grpcRequest.Columns.AddRange(request.Columns.Select(c => new ColumnDefinition { Name = c.Name, Type = c.Type }));

    await client.CreateTableAsync(grpcRequest);
    return Results.Ok(new { message = $"Table '{request.TableName}' created successfully." });
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
public class CreateTableRequestBff
{
    public string TableName { get; set; } = "";
    public List<ColumnDefinitionBff> Columns { get; set; } = new();
}

public class ColumnDefinitionBff
{
    public string Name { get; set; } = "";
    public string Type { get; set; } = "";
}

public class InsertDataRequestBff
{
    public string TableName { get; set; } = "";
    public Dictionary<string, object> Data { get; set; } = new();
}