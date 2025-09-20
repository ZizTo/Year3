using Npgsql; // ИЗМЕНЕНО: Используем Npgsql вместо OleDb
using System.Text.Json;
using System.Text.Json.Serialization;


var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

// ИЗМЕНЕНО: Строка подключения для PostgreSQL
// Убедитесь, что имена пользователя, пароль и база данных соответствуют тем, что вы создали ранее.
string connectionString = "Host=localhost;Username=lab1user;Password=lab1userPassword;Database=crittersdb;";

app.MapPost("/api/create-table", async (TableDefinition tableDef) =>
{
    // Проверяем, что данные от клиента корректны
    if (string.IsNullOrWhiteSpace(tableDef.TableName) || tableDef.Columns.Count == 0)
    {
        return Results.BadRequest(new { message = "Table name and at least one column are required." });
    }

    // Безопасная валидация типов данных
    var allowedTypes = new HashSet<string>(StringComparer.OrdinalIgnoreCase)
    {
        "VARCHAR(255)", "TEXT", "INTEGER", "BOOLEAN", "TIMESTAMP"
    };

    var columnsSql = new List<string>();
    foreach (var col in tableDef.Columns)
    {
        if (string.IsNullOrWhiteSpace(col.Name) || !allowedTypes.Contains(col.Type))
        {
            return Results.BadRequest(new { message = $"Invalid column name or type '{col.Type}'." });
        }
        // Формируем часть SQL-запроса, оборачивая имена в кавычки для безопасности
        columnsSql.Add($"\"{col.Name}\" {col.Type}");
    }

    var tableName = tableDef.TableName;
    var createTableSql = $"CREATE TABLE IF NOT EXISTS \"{tableName}\" ({string.Join(", ", columnsSql)})";

    try
    {
        await using var connection = new NpgsqlConnection(connectionString);
        await connection.OpenAsync();
        await using var command = new NpgsqlCommand(createTableSql, connection);
        await command.ExecuteNonQueryAsync();
        return Results.Ok(new { message = $"Table '{tableName}' created successfully." });
    }
    catch (Exception ex)
    {
        return Results.Problem($"Server error: {ex.Message}");
    }
});

// --- API ЭНДПОИНТ ДЛЯ ВСТАВКИ ДАННЫХ ---
app.MapPost("/api/insert-data", async (DataInsertionRequest request) =>
{
    if (string.IsNullOrWhiteSpace(request.TableName) || request.Data.Count == 0)
    {
        return Results.BadRequest(new { message = "Invalid data for insertion." });
    }

    var tableName = Sanitize(request.TableName);
    var columnNames = request.Data.Keys.Select(Sanitize).ToList();
    
    var columnList = string.Join(", ", columnNames.Select(c => $"\"{c}\""));
    var parameterList = string.Join(", ", columnNames.Select(c => $"@{c}"));
    
    var insertSql = $"INSERT INTO \"{tableName}\" ({columnList}) VALUES ({parameterList})";

    try
    {
        await using var connection = new NpgsqlConnection(connectionString);
        await connection.OpenAsync();
        await using var command = new NpgsqlCommand(insertSql, connection);

        foreach (var colName in columnNames)
        {
            var value = request.Data[colName];
            // Пустые строки заменяем на NULL для совместимости с не-текстовыми типами
            command.Parameters.AddWithValue($"@{colName}", string.IsNullOrEmpty(value?.ToString()) ? DBNull.Value : value);
        }

        await command.ExecuteNonQueryAsync();
        return Results.Ok(new { message = "Data inserted successfully." });
    }
    catch (Exception ex)
    {
        return Results.Problem($"Server error: {ex.Message}");
    }
});

// Настройка для раздачи статических файлов (нашего index.html)
app.UseDefaultFiles();
app.UseStaticFiles();

app.Run();

// --- ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ И ФУНКЦИИ ---

// Классы для автоматического преобразования JSON в C# объекты
public class TableDefinition
{
    [JsonPropertyName("tableName")]
    public string TableName { get; set; } = "";
    [JsonPropertyName("columns")]
    public List<ColumnDefinition> Columns { get;  set; } = new();
}

public class ColumnDefinition
{
    [JsonPropertyName("name")]
    public string Name { get; set; } = "";
    [JsonPropertyName("type")]
    public string Type { get; set; } = "";
}

public class DataInsertionRequest
{
    [JsonPropertyName("tableName")]
    public string TableName { get; set; } = "";
    [JsonPropertyName("data")]
    public Dictionary<string, object> Data { get; set; } = new();
}

// Функция для "очистки" имен таблиц и колонок от опасных символов
/*
string Sanitize(string input)
{
    return new string(input.Where(c => char.IsLetterOrDigit(c) || c == '_').ToArray());
}*/
