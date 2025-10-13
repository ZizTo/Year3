using DatabaseGrpcService;
using DatabaseGrpcService.Services;
using Google.Protobuf.WellKnownTypes;
using Grpc.Core;
using Grpc.Net.Client;
using Grpc.Net.Client.Web;
using System.Text.Json;
using System.Text.Json.Nodes;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddGrpcClient<DatabaseManager.DatabaseManagerClient>(o =>
{
    o.Address = new Uri("http://localhost:5074");
})
.ConfigurePrimaryHttpMessageHandler(() =>
 {
     return new GrpcWebHandler(new HttpClientHandler());
 });

builder.Services.AddGrpc(options =>
{
    options.EnableDetailedErrors = true;
});

var app = builder.Build();

app.MapGet("/api/tables", async (DatabaseManager.DatabaseManagerClient client) =>
{
    var response = await client.GetTablesAsync(new Empty());
    return Results.Ok(response.TableNames);
});

app.MapGet("/api/tables/{tableName}/schema", async (string tableName, DatabaseManager.DatabaseManagerClient client) =>
{
    var request = new GetTableSchemaRequest { TableName = tableName };
    var response = await client.GetTableSchemaAsync(request);
    return Results.Ok(response.Columns);
});

app.MapGet("/api/tables/{tableName}/data", async (string tableName, DatabaseManager.DatabaseManagerClient client) =>
{
    var request = new GetTableDataRequest { TableName = tableName };
    var response = await client.GetTableDataAsync(request);
    var result = response.Rows.Select(row =>
        row.Fields.ToDictionary(
            f => f.Key,
            f => ProtoConverter.ToObject(f.Value)
        )
    );
    return Results.Ok(result);
});


app.MapPost("/api/tables", async (CreateTableRequestBff request, DatabaseManager.DatabaseManagerClient client) =>
{
    var grpcRequest = new TableDefinition { TableName = request.TableName };
    grpcRequest.Columns.AddRange(request.Columns.Select(c => new ColumnDefinition { Name = c.Name, Type = c.Type }));

    await client.CreateTableAsync(grpcRequest);
    return Results.Ok(new { message = $"Table '{request.TableName}' created successfully." });
});

app.MapPost("/api/data", async (InsertDataRequestBff request, DatabaseManager.DatabaseManagerClient client) =>
{
    try
    {
        var grpcRequest = new InsertDataRequest { TableName = request.TableName };
        foreach (var pair in request.Data)
        {
            object? csharpValue = pair.Value is JsonValue val ? val.GetValue<object>() : null;

            grpcRequest.Data.Add(pair.Key, ProtoConverter.ToValue(csharpValue));
        }

        await client.InsertDataAsync(grpcRequest);
        return Results.Ok(new { message = "Data inserted successfully." });
    }
    catch (RpcException ex)
    {
        return Results.Problem(detail: ex.Status.Detail, statusCode: 500, title: "gRPC Error");
    }
});

app.UseDefaultFiles();
app.UseStaticFiles();

app.Run();

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
    public JsonObject Data { get; set; } = new();
}
