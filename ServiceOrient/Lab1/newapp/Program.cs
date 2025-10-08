using Npgsql;
using System.Text.Json.Serialization;

var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

string connectionString = "Host=localhost;Username=lab1user;Password=lab1userPassword;Database=crittersdb;";

string Sanitize(string input) => new string(input.Where(c => char.IsLetterOrDigit(c) || c == '_').ToArray());

// --- ЭНДПОИНТ: Получить список всех таблиц ---
app.MapGet("/api/tables", async () =>
{
    var tables = new List<string>();
    // ИЗМЕНЕНО: SQL-запрос для SQL Server
    var sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE'";
    try
    {
        await using var connection = new SqlConnection(connectionString); // ИЗМЕНЕНО: SqlConnection
        await connection.OpenAsync();
        await using var command = new SqlCommand(sql, connection); // ИЗМЕНЕНО: SqlCommand
        await using var reader = await command.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            tables.Add(reader.GetString(0));
        }
        return Results.Ok(tables);
    }
    catch (Exception ex) { return Results.Problem(ex.Message); }
});

// --- ЭНДПОИНТ: Получить структуру (колонки) одной таблицы ---
app.MapGet("/api/tables/{tableName}", async (string tableName) =>
{
    var columns = new List<ColumnDefinition>();
    // ИЗМЕНЕНО: SQL-запрос для SQL Server
    var sql = "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = @tableName";
    try
    {
        await using var connection = new SqlConnection(connectionString);
        await connection.OpenAsync();
        await using var command = new SqlCommand(sql, connection);
        command.Parameters.AddWithValue("@tableName", Sanitize(tableName));
        await using var reader = await command.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            columns.Add(new ColumnDefinition { Name = reader.GetString(0), Type = reader.GetString(1).ToUpperInvariant() });
        }
        return Results.Ok(columns);
    }
    catch (Exception ex) { return Results.Problem(ex.Message); }
});

// --- ЭНДПОИНТ: Получить все данные (строки) из таблицы ---
app.MapGet("/api/tables/{tableName}/data", async (string tableName) =>
{
    var rows = new List<Dictionary<string, object>>();
    // ИЗМЕНЕНО: SQL Server использует квадратные скобки [] для экранирования имен
    var sql = $"SELECT * FROM [{Sanitize(tableName)}]";
    try
    {
        await using var connection = new SqlConnection(connectionString);
        await connection.OpenAsync();
        await using var command = new SqlCommand(sql, connection);
        await using var reader = await command.ExecuteReaderAsync();

        while (await reader.ReadAsync())
        {
            var row = new Dictionary<string, object>();
            for (int i = 0; i < reader.FieldCount; i++)
            {
                row[reader.GetName(i)] = reader.GetValue(i) is DBNull ? null : reader.GetValue(i);
            }
            rows.Add(row);
        }
        return Results.Ok(rows);
    }
    catch (Exception ex) { return Results.Problem(ex.Message); }
});

// --- ЭНДПОИНТ: Создать таблицу ---
app.MapPost("/api/create-table", async (TableDefinition tableDef) =>
{
    if (string.IsNullOrWhiteSpace(tableDef.TableName) || tableDef.Columns.Count == 0)
        return Results.BadRequest(new { message = "Table name and at least one column are required." });
    
    // ИЗМЕНЕНО: Типы данных для SQL Server
    var allowedTypes = new HashSet<string>(StringComparer.OrdinalIgnoreCase) { "NVARCHAR(255)", "NVARCHAR(MAX)", "INT", "BIT", "DATETIME2" };
    var columnsSql = new List<string>();
    foreach (var col in tableDef.Columns)
    {
        if (string.IsNullOrWhiteSpace(col.Name) || !allowedTypes.Contains(col.Type))
            return Results.BadRequest(new { message = $"Invalid column name or type '{col.Type}'." });
        columnsSql.Add($"[{Sanitize(col.Name)}] {col.Type}");
    }
    
    // ИЗМЕНЕНО: Убрано IF NOT EXISTS (в SQL Server другой синтаксис, проще обработать ошибку)
    var createTableSql = $"CREATE TABLE [{Sanitize(tableDef.TableName)}] ({string.Join(", ", columnsSql)})";
    try
    {
        await using var connection = new SqlConnection(connectionString);
        await connection.OpenAsync();
        await using var command = new SqlCommand(createTableSql, connection);
        await command.ExecuteNonQueryAsync();
        return Results.Ok(new { message = $"Table '{Sanitize(tableDef.TableName)}' created successfully." });
    }
    catch (Exception ex) { return Results.Problem($"Server error: {ex.Message}"); }
});

// --- ЭНДПОИНТ: Вставить данные ---
app.MapPost("/api/insert-data", async (DataInsertionRequest request) =>
{
    if (string.IsNullOrWhiteSpace(request.TableName) || request.Data.Count == 0)
        return Results.BadRequest(new { message = "Invalid data for insertion." });

    var tableName = Sanitize(request.TableName);
    
    try
    {
        await using var connection = new SqlConnection(connectionString);
        await connection.OpenAsync();
        var columnTypes = new Dictionary<string, string>();
        var schemaSql = "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = @tableName";
        await using (var schemaCmd = new SqlCommand(schemaSql, connection))
        {
            schemaCmd.Parameters.AddWithValue("@tableName", tableName);
            await using (var reader = await schemaCmd.ExecuteReaderAsync())
            {
                while (await reader.ReadAsync())
                {
                    columnTypes[reader.GetString(0)] = reader.GetString(1).ToUpperInvariant();
                }
            }
        }

        var columnNames = request.Data.Keys.Select(Sanitize).ToList();
        var columnList = string.Join(", ", columnNames.Select(c => $"[{c}]"));
        var parameterList = string.Join(", ", columnNames.Select(c => $"@{c}"));
        var insertSql = $"INSERT INTO [{tableName}] ({columnList}) VALUES ({parameterList})";

        await using var command = new SqlCommand(insertSql, connection);
        foreach (var colName in columnNames)
        {
            var rawValue = request.Data[colName];
            object parameterValue;

            if (rawValue is null || (rawValue is string s && string.IsNullOrWhiteSpace(s))) {
                parameterValue = DBNull.Value;
            } else {
                var columnType = columnTypes.GetValueOrDefault(colName, "NVARCHAR");
                var valueString = rawValue.ToString();
                switch (columnType) {
                    case "INT":
                        parameterValue = int.Parse(valueString);
                        break;
                    case "DATETIME2":
                        parameterValue = DateTime.Parse(valueString);
                        break;
                    case "BIT":
                        var lowerValue = valueString.ToLowerInvariant();
                        parameterValue = (lowerValue == "true" || lowerValue == "t" || lowerValue == "yes" || lowerValue == "1");
                        break;
                    default:
                        parameterValue = valueString;
                        break;
                }
            }
            command.Parameters.AddWithValue($"@{colName}", parameterValue);
        }
        await command.ExecuteNonQueryAsync();
        return Results.Ok(new { message = "Data inserted successfully." });
    }
    catch (Exception ex) { return Results.Problem($"Server error: {ex.Message}"); }
});

app.UseDefaultFiles();
app.UseStaticFiles();
app.Run();

// --- Вспомогательные классы (без изменений) ---
public class TableDefinition { [JsonPropertyName("tableName")] public string TableName { get; set; } = ""; [JsonPropertyName("columns")] public List<ColumnDefinition> Columns { get; set; } = new(); }
public class ColumnDefinition { [JsonPropertyName("name")] public string Name { get; set; } = ""; [JsonPropertyName("type")] public string Type { get; set; } = ""; }
public class DataInsertionRequest { [JsonPropertyName("tableName")] public string TableName { get; set; } = ""; [JsonPropertyName("data")] public Dictionary<string, object> Data { get; set; } = new(); }
