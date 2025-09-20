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
    var sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE'";
    try
    {
        await using var connection = new NpgsqlConnection(connectionString);
        await connection.OpenAsync();
        await using var command = new NpgsqlCommand(sql, connection);
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
    var sql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = @tableName";
    try
    {
        await using var connection = new NpgsqlConnection(connectionString);
        await connection.OpenAsync();
        await using var command = new NpgsqlCommand(sql, connection);
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
    var sanitizedTableName = Sanitize(tableName);
    var sql = $"SELECT * FROM \"{sanitizedTableName}\""; 
    try
    {
        await using var connection = new NpgsqlConnection(connectionString);
        await connection.OpenAsync();
        await using var command = new NpgsqlCommand(sql, connection);
        await using var reader = await command.ExecuteReaderAsync();

        while (await reader.ReadAsync())
        {
            var row = new Dictionary<string, object>();
            for (int i = 0; i < reader.FieldCount; i++)
            {
                var value = reader.GetValue(i);
                row[reader.GetName(i)] = value is DBNull ? null : value;
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
    var allowedTypes = new HashSet<string>(StringComparer.OrdinalIgnoreCase) { "VARCHAR(255)", "TEXT", "INTEGER", "BOOLEAN", "TIMESTAMP" };
    var columnsSql = new List<string>();
    foreach (var col in tableDef.Columns)
    {
        if (string.IsNullOrWhiteSpace(col.Name) || !allowedTypes.Contains(col.Type))
            return Results.BadRequest(new { message = $"Invalid column name or type '{col.Type}'." });
        columnsSql.Add($"\"{Sanitize(col.Name)}\" {col.Type}");
    }
    var createTableSql = $"CREATE TABLE IF NOT EXISTS \"{Sanitize(tableDef.TableName)}\" ({string.Join(", ", columnsSql)})";
    try
    {
        await using var connection = new NpgsqlConnection(connectionString);
        await connection.OpenAsync();
        await using var command = new NpgsqlCommand(createTableSql, connection);
        await command.ExecuteNonQueryAsync();
        return Results.Ok(new { message = $"Table '{Sanitize(tableDef.TableName)}' created successfully." });
    }
    catch (Exception ex) { return Results.Problem($"Server error: {ex.Message}"); }
});

// --- ЭНДПОИНТ: Вставить данные (С ИСПРАВЛЕНИЯМИ) ---
app.MapPost("/api/insert-data", async (DataInsertionRequest request) =>
{
    if (string.IsNullOrWhiteSpace(request.TableName) || request.Data.Count == 0)
        return Results.BadRequest(new { message = "Invalid data for insertion." });

    var tableName = Sanitize(request.TableName);
    
    try
    {
        await using var connection = new NpgsqlConnection(connectionString);
        await connection.OpenAsync();

        // 1. СНАЧАЛА ПОЛУЧАЕМ СХЕМУ (типы колонок) ТАБЛИЦЫ
        var columnTypes = new Dictionary<string, string>();
        var schemaSql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = @tableName";
        await using (var schemaCmd = new NpgsqlCommand(schemaSql, connection))
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

        // 2. ТЕПЕРЬ СОБИРАЕМ ЗАПРОС И ПРАВИЛЬНО ПРЕОБРАЗУЕМ ДАННЫЕ
        var columnNames = request.Data.Keys.Select(Sanitize).ToList();
        var columnList = string.Join(", ", columnNames.Select(c => $"\"{c}\""));
        var parameterList = string.Join(", ", columnNames.Select(c => $"@{c}"));
        var insertSql = $"INSERT INTO \"{tableName}\" ({columnList}) VALUES ({parameterList})";

        await using var command = new NpgsqlCommand(insertSql, connection);
        foreach (var colName in columnNames)
        {
            var rawValue = request.Data[colName];
            object parameterValue;

            if (rawValue is null || (rawValue is string s && string.IsNullOrWhiteSpace(s))) {
                parameterValue = DBNull.Value;
            } else {
                var columnType = columnTypes.GetValueOrDefault(colName, "TEXT"); // Узнаем тип колонки
                var valueString = rawValue.ToString();

                switch (columnType) {
                    case "INTEGER":
                        parameterValue = int.Parse(valueString);
                        break;
                    case "TIMESTAMP WITHOUT TIME ZONE":
                    case "TIMESTAMP":
                        parameterValue = DateTime.Parse(valueString);
                        break;
                    case "BOOLEAN":
                        var lowerValue = valueString.ToLowerInvariant();
                        parameterValue = (lowerValue == "true" || lowerValue == "t" || lowerValue == "yes" || lowerValue == "1");
                        break;
                    default: // Для VARCHAR, TEXT и других
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

// --- Вспомогательные классы ---
public class TableDefinition { [JsonPropertyName("tableName")] public string TableName { get; set; } = ""; [JsonPropertyName("columns")] public List<ColumnDefinition> Columns { get; set; } = new(); }
public class ColumnDefinition { [JsonPropertyName("name")] public string Name { get; set; } = ""; [JsonPropertyName("type")] public string Type { get; set; } = ""; }
public class DataInsertionRequest { [JsonPropertyName("tableName")] public string TableName { get; set; } = ""; [JsonPropertyName("data")] public Dictionary<string, object> Data { get; set; } = new(); }
