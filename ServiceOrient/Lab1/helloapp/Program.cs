using Npgsql; // ИЗМЕНЕНО: Используем Npgsql вместо OleDb
using System.Text.Json;
using System.Text.Json.Serialization;


var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

// ИЗМЕНЕНО: Строка подключения для PostgreSQL
// Убедитесь, что имена пользователя, пароль и база данных соответствуют тем, что вы создали ранее.
string connectionString = "Host=localhost;Username=lab1user;Password=lab1userPassword;Database=crittersdb;";

// --- API Endpoint to Create a Table ---
app.MapPost("/api/create-table", async (HttpContext context) =>
{
    try
    {
        using var reader = new StreamReader(context.Request.Body);
        var requestBody = await reader.ReadToEndAsync();
        var tableDefinition = JsonSerializer.Deserialize<TableDefinition>(requestBody);

        if (tableDefinition == null || string.IsNullOrWhiteSpace(tableDefinition.TableName) || tableDefinition.Columns.Count == 0)
        {
            return Results.BadRequest(new { message = "Invalid table definition." });
        }

        var tableName = Sanitize(tableDefinition.TableName);
        var columnsSql = new List<string>();
        
        // --- НАЧАЛО ИЗМЕНЕНИЙ ---

        // Создаем список разрешенных типов данных на сервере.
        // Это гарантирует, что в SQL-запрос не попадет ничего лишнего.
        var allowedTypes = new HashSet<string>(StringComparer.OrdinalIgnoreCase)
        {
            "VARCHAR(255)", "TEXT", "INTEGER", "BOOLEAN", "TIMESTAMP"
        };

        foreach (var col in tableDefinition.Columns)
        {
            if (string.IsNullOrWhiteSpace(col.Name) || string.IsNullOrWhiteSpace(col.Type)) continue;

            // Проверяем, есть ли тип, присланный клиентом, в нашем списке разрешенных.
            if (!allowedTypes.Contains(col.Type))
            {
                // Если типа нет в списке, возвращаем ошибку.
                return Results.BadRequest(new { message = $"Data type '{col.Type}' is not allowed." });
            }

            // Теперь мы уверены, что тип безопасен.
            // Применяем Sanitize ТОЛЬКО к имени колонки, а тип используем как есть.
            // Имя колонки оборачиваем в двойные кавычки - это хорошая практика для PostgreSQL.
            columnsSql.Add($"\"{Sanitize(col.Name)}\" {col.Type}");
        }
        
        // --- КОНЕЦ ИЗМЕНЕНИЙ ---

        if (columnsSql.Count == 0)
        {
             return Results.BadRequest(new { message = "No valid columns provided." });
        }

        // Имя таблицы тоже оборачиваем в двойные кавычки.
        var createTableSql = $"CREATE TABLE \"{tableName}\" ({string.Join(", ", columnsSql)})";
        
        // Выводим финальный SQL-запрос в консоль для отладки
        Console.WriteLine($"Executing SQL: {createTableSql}");

        await using (var connection = new NpgsqlConnection(connectionString))
        {
            await connection.OpenAsync();
            await using (var command = new NpgsqlCommand(createTableSql, connection))
            {
                await command.ExecuteNonQueryAsync();
            }
        }

        return Results.Ok(new { message = $"Table '{tableName}' created successfully." });
    }
    catch (Exception ex)
    {
        Console.WriteLine("--- AN ERROR OCCURRED ---");
        Console.WriteLine($"Error Message: {ex.Message}");
        Console.WriteLine($"Stack Trace: {ex.StackTrace}");
        
        return Results.Problem($"An error occurred on the server: {ex.Message}");
    }
});

// --- API Endpoint to Insert Data ---
// --- API Endpoint to Insert Data ---
app.MapPost("/api/insert-data", async (HttpContext context) =>
{
    try
    {
        using var reader = new StreamReader(context.Request.Body);
        var requestBody = await reader.ReadToEndAsync();
        var dataInsertion = JsonSerializer.Deserialize<DataInsertionRequest>(requestBody);

        if (dataInsertion == null || string.IsNullOrWhiteSpace(dataInsertion.TableName) || dataInsertion.Data.Count == 0)
        {
            return Results.BadRequest(new { message = "Invalid data for insertion." });
        }
        
        var tableName = Sanitize(dataInsertion.TableName);
        var columnNames = dataInsertion.Data.Keys.Select(c => Sanitize(c)).ToList();
        var parameterNames = string.Join(", ", columnNames.Select(c => $"@{c}"));
        var columnList = string.Join(", ", columnNames.Select(c => $"\"{c}\""));
        
        var insertSql = $"INSERT INTO \"{tableName}\" ({columnList}) VALUES ({parameterNames})";

        // --- ДОБАВЛЕНО ЛОГИРОВАНИЕ ---
        Console.WriteLine($"Executing SQL: {insertSql}");

        await using (var connection = new NpgsqlConnection(connectionString))
        {
            await connection.OpenAsync();
            await using (var command = new NpgsqlCommand(insertSql, connection))
            {
                foreach (var colName in columnNames)
                {
                    // Получаем значение. Если это пустая строка, заменим на DBNull.Value
                    // Это важно для числовых и других нетекстовых полей.
                    var valueObject = dataInsertion.Data[colName];
                    var value = (valueObject is string s && string.IsNullOrEmpty(s)) ? DBNull.Value : valueObject;

                    Console.WriteLine($"  - Parameter: @{colName} = '{value}'");
                    command.Parameters.AddWithValue($"@{colName}", value);
                }
                await command.ExecuteNonQueryAsync();
            }
        }
        return Results.Ok(new { message = "Data inserted successfully." });
    }
    catch (Exception ex)
    {
        // --- УЛУЧШЕННЫЙ БЛОК CATCH ---
        Console.WriteLine("--- AN ERROR OCCURRED (INSERT-DATA) ---");
        Console.WriteLine($"Error Message: {ex.Message}");
        Console.WriteLine($"Stack Trace: {ex.StackTrace}");
        
        return Results.Problem($"An error occurred on the server: {ex.Message}");
    }
});



// Serve the HTML file as the default page
app.MapGet("/", (HttpContext context) => {
    context.Response.ContentType = "text/html; charset=utf-8";
    return context.Response.SendFileAsync("html/index.html");
});


app.Run();


// --- Helper Classes and Functions ---
// PostgreSQL может использовать кавычки для имен, но для простоты оставим эту функцию
// --- Helper Classes and Functions ---

// Просто хорошая практика для предотвращения SQL-инъекций
string Sanitize(string input)
{
    return new string(input.Where(c => char.IsLetterOrDigit(c) || c == '_').ToArray());
}

public class TableDefinition
{
    // Атрибут явно указывает, что свойство C# "TableName" соответствует свойству "tableName" в JSON.
    [JsonPropertyName("tableName")]
    public string TableName { get; set; }

    [JsonPropertyName("columns")]
    public List<ColumnDefinition> Columns { get; set; } = new(); // Добавил = new() - это хорошая практика
}

public class ColumnDefinition
{
    [JsonPropertyName("name")]
    public string Name { get; set; }

    [JsonPropertyName("type")]
    public string Type { get; set; }
}

public class DataInsertionRequest
{
    // Также добавим атрибуты и сюда для единообразия
    [JsonPropertyName("tableName")]
    public string TableName { get; set; }

    [JsonPropertyName("data")]
    public Dictionary<string, object> Data { get; set; }
}