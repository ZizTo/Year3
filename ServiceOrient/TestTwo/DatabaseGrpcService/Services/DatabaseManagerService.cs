using Google.Protobuf.WellKnownTypes;
using Grpc.Core;
using Microsoft.Data.SqlClient;
using System.Data;
using System.Globalization;
using System.Text.RegularExpressions;

namespace DatabaseGrpcService.Services;

// Наследуемся от авто-сгенерированного базового класса
public class DatabaseManagerService : DatabaseManager.DatabaseManagerBase
{
    private readonly IConfiguration _configuration;
    private readonly string _connectionString;
    private static readonly Regex SafeIdentifierRegex = new(@"^[a-zA-Z_][a-zA-Z0-9_]{0,127}$", RegexOptions.Compiled);
    private static readonly HashSet<string> AllowedSqlTypes = new(StringComparer.OrdinalIgnoreCase)
    {
        "NVARCHAR(255)", "NVARCHAR(MAX)", "INT", "BIGINT", "BIT", "DATETIME2", "FLOAT"
    };

    public DatabaseManagerService(IConfiguration configuration)
    {
        _configuration = configuration;
        _connectionString = configuration.GetConnectionString("CrittersDb");
        if (string.IsNullOrEmpty(_connectionString))
        {
            // Эта ошибка будет более информативной, если проблема в конфигурации.
            throw new InvalidOperationException("Connection string 'CrittersDb' not found or is empty in appsettings.json.");
        }

    }

    // Реализуем каждый метод из .proto файла
    public override async Task<GetTablesResponse> GetTables(Empty request, ServerCallContext context)
    {
        var response = new GetTablesResponse();
        const string sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_NAME";

        await using var connection = new SqlConnection(_connectionString);
        await connection.OpenAsync();
        await using var command = new SqlCommand(sql, connection);
        await using var reader = await command.ExecuteReaderAsync();

        while (await reader.ReadAsync())
        {
            response.TableNames.Add(reader.GetString(0));
        }
        return response;
    }

    public override async Task<GetTableSchemaResponse> GetTableSchema(GetTableSchemaRequest request, ServerCallContext context)
    {
        ValidateIdentifier(request.TableName);
        var response = new GetTableSchemaResponse();
        const string sql = "SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = @tableName ORDER BY ORDINAL_POSITION";

        await using var connection = new SqlConnection(_connectionString);
        await connection.OpenAsync();
        await using var command = new SqlCommand(sql, connection);
        command.Parameters.AddWithValue("@tableName", request.TableName);

        await using var reader = await command.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            var type = reader.GetString(1).ToUpperInvariant();
            if (type == "NVARCHAR" && !reader.IsDBNull(2))
            {
                var length = reader.GetInt32(2);
                type = length == -1 ? "NVARCHAR(MAX)" : $"NVARCHAR({length})";
            }
            // Добавляем найденную колонку в ответ
            response.Columns.Add(new ColumnDefinition { Name = reader.GetString(0), Type = type });
        }
        return response;
    }

    public override async Task<GetTableDataResponse> GetTableData(GetTableDataRequest request, ServerCallContext context)
    {
        ValidateIdentifier(request.TableName);
        var response = new GetTableDataResponse();
        var sql = $"SELECT * FROM [{request.TableName}]";

        await using var conn = new SqlConnection(_connectionString);
        await conn.OpenAsync();
        await using var cmd = new SqlCommand(sql, conn);
        await using var reader = await cmd.ExecuteReaderAsync();

        while (await reader.ReadAsync())
        {
            var row = new Struct();
            for (int i = 0; i < reader.FieldCount; i++)
            {
                // ++ ИЗМЕНЕНИЕ ЗДЕСЬ ++
                // Было: Value.ForObject(reader.GetValue(i))
                // Стало: ProtoConverter.ToValue(reader.GetValue(i))
                row.Fields.Add(reader.GetName(i), ProtoConverter.ToValue(reader.GetValue(i)));
            }
            response.Rows.Add(row);
        }
        return response;
    }


    public override async Task<Empty> CreateTable(TableDefinition request, ServerCallContext context)
    {
        // 1. Валидация входных данных
        ValidateIdentifier(request.TableName);
        if (request.Columns == null || request.Columns.Count == 0)
        {
            // В gRPC мы выбрасываем RpcException со статусом
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Table must have at least one column."));
        }

        var columnsSql = new List<string>();
        foreach (var col in request.Columns)
        {
            ValidateIdentifier(col.Name);
            if (string.IsNullOrWhiteSpace(col.Type) || !AllowedSqlTypes.Contains(col.Type))
            {
                throw new RpcException(new Status(StatusCode.InvalidArgument, $"Column '{col.Name}' has an invalid or disallowed type '{col.Type}'."));
            }
            // Для простоты все создаваемые поля будут nullable
            columnsSql.Add($"[{col.Name}] {col.Type} NULL");
        }

        // 2. Построение безопасного SQL-запроса
        var createTableSql = $"CREATE TABLE [{request.TableName}] ({string.Join(", ", columnsSql)})";

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
            // Если таблица уже существует или другая SQL-ошибка
            throw new RpcException(new Status(StatusCode.AlreadyExists, $"SQL Error: {ex.Message}"));
        }

        // В случае успеха возвращаем пустой ответ
        return new Empty();
    }

    public override async Task<Empty> InsertData(InsertDataRequest request, ServerCallContext context)
    {
        ValidateIdentifier(request.TableName);
        if (request.Data == null || request.Data.Count == 0)
        {
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Cannot insert empty data."));
        }

        var columnNames = new List<string>();
        var parameterNames = new List<string>();
        var parameters = new List<SqlParameter>();

        foreach (var pair in request.Data)
        {
            ValidateIdentifier(pair.Key);
            columnNames.Add($"[{pair.Key}]");
            var paramName = $"@{pair.Key}";
            parameterNames.Add(paramName);
            parameters.Add(new SqlParameter(paramName, ConvertFromValue(pair.Value)));
        }

        var sql = $"INSERT INTO [{request.TableName}] ({string.Join(", ", columnNames)}) VALUES ({string.Join(", ", parameterNames)})";

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
            throw new RpcException(new Status(StatusCode.Internal, $"SQL Error: {ex.Message}"));
        }

        return new Empty();
    }

    // --- Вспомогательные методы ---
    private static void ValidateIdentifier(string identifier)
    {
        if (string.IsNullOrWhiteSpace(identifier) || !SafeIdentifierRegex.IsMatch(identifier))
            throw new RpcException(new Status(StatusCode.InvalidArgument, $"'{identifier}' is not a valid identifier."));
    }

    private static object? ConvertFromValue(Value protoValue)
    {
        switch (protoValue.KindCase)
        {
            case Value.KindOneofCase.NullValue:
                return DBNull.Value;

            case Value.KindOneofCase.NumberValue:
                return protoValue.NumberValue;

            case Value.KindOneofCase.BoolValue:
                return protoValue.BoolValue;

            case Value.KindOneofCase.StringValue:
                var strValue = protoValue.StringValue;

                // Пытаемся распознать строку как дату.
                // DateTimeStyles.AdjustToUniversal важен для правильной обработки часовых поясов.
                if (DateTime.TryParse(strValue, CultureInfo.InvariantCulture, DateTimeStyles.AdjustToUniversal, out var dateTime))
                {
                    // Если получилось - возвращаем настоящий объект DateTime.
                    // SqlParameter обожает получать DateTime и сам его правильно передаст в SQL.
                    return dateTime;
                }

                // Если это не дата, просто возвращаем строку.
                return strValue;

            default:
                throw new RpcException(new Status(StatusCode.InvalidArgument, "Unsupported value type."));
        }
    }
}
