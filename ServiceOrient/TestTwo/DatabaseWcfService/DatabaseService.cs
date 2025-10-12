// DatabaseService.cs
using CoreWCF;
using CoreWCF.Web;
using Microsoft.Data.SqlClient;
using System.Data;
using System.Net;
using System.Text.RegularExpressions;

namespace DatabaseWcfService;

[ServiceBehavior(AddressFilterMode = AddressFilterMode.Prefix)]
public class DatabaseService : IDatabaseService
{
    private readonly IConfiguration _configuration;
    private readonly string _connectionString;

    // Безопасный Regex для имен таблиц и столбцов (начинается с буквы/_, далее буквы, цифры, _)
    private static readonly Regex SafeIdentifierRegex = new(@"^[a-zA-Z_][a-zA-Z0-9_]{0,127}$", RegexOptions.Compiled);

    // Белый список разрешенных типов данных SQL
    private static readonly HashSet<string> AllowedSqlTypes = new(StringComparer.OrdinalIgnoreCase)
    {
        "NVARCHAR(255)", "NVARCHAR(MAX)", "INT", "BIGINT", "BIT", "DATETIME2", "FLOAT"
    };

    public DatabaseService(IConfiguration configuration)
    {
        _configuration = configuration;
        _connectionString = _configuration.GetConnectionString("CrittersDb")
            ?? throw new InvalidOperationException("Connection string 'CrittersDb' not found in configuration.");
    }

    public async Task<List<string>> GetTablesAsync()
    {
        var tables = new List<string>();
        const string sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_NAME";

        await using var connection = new SqlConnection(_connectionString);
        await connection.OpenAsync();
        await using var command = new SqlCommand(sql, connection);
        await using var reader = await command.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            tables.Add(reader.GetString(0));
        }
        return tables;
    }

    public async Task<List<ColumnDefinition>> GetTableSchemaAsync(string tableName)
    {
        ValidateIdentifier(tableName);
        var columns = new List<ColumnDefinition>();
        const string sql = "SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = @tableName ORDER BY ORDINAL_POSITION";

        await using var connection = new SqlConnection(_connectionString);
        await connection.OpenAsync();
        await using var command = new SqlCommand(sql, connection);
        command.Parameters.AddWithValue("@tableName", tableName);

        await using var reader = await command.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            var type = reader.GetString(1).ToUpperInvariant();
            if (type == "NVARCHAR" && !reader.IsDBNull(2))
            {
                var length = reader.GetInt32(2);
                type = length == -1 ? "NVARCHAR(MAX)" : $"NVARCHAR({length})";
            }
            columns.Add(new ColumnDefinition { name = reader.GetString(0), type = type });
        }
        return columns;
    }

    public async Task<List<Dictionary<string, object>>> GetTableDataAsync(string tableName)
    {
        ValidateIdentifier(tableName);
        var rows = new List<Dictionary<string, object>>();
        var sql = $"SELECT * FROM [{tableName}]"; // Валидация выше защищает от инъекции

        await using var connection = new SqlConnection(_connectionString);
        await connection.OpenAsync();
        await using var command = new SqlCommand(sql, connection);
        await using var reader = await command.ExecuteReaderAsync();

        while (await reader.ReadAsync())
        {
            var row = new Dictionary<string, object>(StringComparer.OrdinalIgnoreCase);
            for (int i = 0; i < reader.FieldCount; i++)
            {
                row[reader.GetName(i)] = reader.IsDBNull(i) ? null : reader.GetValue(i);
            }
            rows.Add(row);
        }
        return rows;
    }

    public async Task CreateTableAsync(TableDefinition tableDef)
    {
        // 1. Валидация входных данных
        ValidateIdentifier(tableDef.tableName);
        if (tableDef.columns == null || tableDef.columns.Count == 0)
        {
            ThrowFault("BAD_REQUEST", "Table must have at least one column.", HttpStatusCode.BadRequest);
        }

        // 2. Построение безопасного SQL-запроса
        var columnsSql = new List<string>();
        foreach (var col in tableDef.columns)
        {
            ValidateIdentifier(col.name);
            if (string.IsNullOrWhiteSpace(col.type) || !AllowedSqlTypes.Contains(col.type))
            {
                ThrowFault("BAD_REQUEST", $"Column '{col.name}' has an invalid or disallowed type '{col.type}'.", HttpStatusCode.BadRequest);
            }
            columnsSql.Add($"[{col.name}] {col.type} NULL"); // Для простоты все поля nullable
        }

        var createTableSql = $"CREATE TABLE [{tableDef.tableName}] ({string.Join(", ", columnsSql)})";

        // 3. Выполнение запроса
        try
        {
            await using var connection = new SqlConnection(_connectionString);
            await connection.OpenAsync();
            await using var command = new SqlCommand(createTableSql, connection);
            await command.ExecuteNonQueryAsync();
        }
        catch (SqlException ex)
        {
            ThrowFault("DB_ERROR", $"SQL Error: {ex.Message}", HttpStatusCode.Conflict); // 409 Conflict если таблица уже есть
        }
    }

    public async Task InsertDataAsync(DataInsertionRequest request)
    {
        var tableName = request.tableName;
        var data = request.data; // Теперь это List<KeyValue>

        ValidateIdentifier(tableName);
        if (data == null || data.Count == 0)
        {
            ThrowFault("BAD_REQUEST", "Cannot insert empty data.", HttpStatusCode.BadRequest);
        }

        var columnNames = new List<string>();
        var parameterNames = new List<string>();
        var parameters = new List<SqlParameter>();

        // ++ ИЗМЕНЯЕМ ЦИКЛ ЗДЕСЬ ++
        foreach (var pair in data) // Раньше было foreach (var pair in data)
        {
            ValidateIdentifier(pair.key);
            columnNames.Add($"[{pair.key}]");
            var paramName = $"@{pair.key}";
            parameterNames.Add(paramName);
            parameters.Add(new SqlParameter(paramName, ParseValueForSql(pair.value)));
        }

        var sql = $"INSERT INTO [{tableName}] ({string.Join(", ", columnNames)}) VALUES ({string.Join(", ", parameterNames)})";

        try
        {
            await using var connection = new SqlConnection(_connectionString);
            await connection.OpenAsync();
            await using var command = new SqlCommand(sql, connection);
            command.Parameters.AddRange(parameters.ToArray());
            await command.ExecuteNonQueryAsync();
        }
        catch (SqlException ex)
        {
            ThrowFault("DB_ERROR", $"SQL Error: {ex.Message}", HttpStatusCode.BadRequest);
        }
    }

    // --- Приватные хелперы ---

    private static void ValidateIdentifier(string? identifier)
    {
        if (string.IsNullOrWhiteSpace(identifier) || !SafeIdentifierRegex.IsMatch(identifier))
        {
            ThrowFault("INVALID_IDENTIFIER", $"'{identifier}' is not a valid name for a table or column.", HttpStatusCode.BadRequest);
        }
    }

    private static object ParseValueForSql(object? rawValue)
    {
        if (rawValue is null || string.IsNullOrEmpty(rawValue.ToString()))
        {
            return System.DBNull.Value;
        }
        // CoreWCF десериализует JSON числа в Int64 (long), а не Int32 (int)
        if (rawValue is long l)
        {
            if (l <= int.MaxValue && l >= int.MinValue) return (int)l;
            return l; // Остается как bigint
        }
        return rawValue; // Остальные типы (string, bool, double) передаются как есть
    }

    private static void ThrowFault(string code, string message, HttpStatusCode statusCode)
    {
        var detail = new ErrorDetail { ErrorCode = code, Message = message };
        throw new WebFaultException<ErrorDetail>(detail, statusCode);
    }
}
