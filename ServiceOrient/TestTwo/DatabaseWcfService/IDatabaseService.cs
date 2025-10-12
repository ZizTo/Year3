// IDatabaseService.cs
using CoreWCF;
using CoreWCF.Web;
using System.Runtime.Serialization;

namespace DatabaseWcfService;

[ServiceContract]
public interface IDatabaseService
{
    /// <summary>
    /// Получает список всех пользовательских таблиц в базе данных.
    /// </summary>
    [OperationContract]
    [WebGet(UriTemplate = "tables", ResponseFormat = WebMessageFormat.Json)]
    Task<List<string>> GetTablesAsync();

    /// <summary>
    /// Получает схему (имена и типы столбцов) для указанной таблицы.
    /// </summary>
    [OperationContract]
    [WebGet(UriTemplate = "tables/{tableName}/schema", ResponseFormat = WebMessageFormat.Json)]
    Task<List<ColumnDefinition>> GetTableSchemaAsync(string tableName);

    /// <summary>
    /// Получает данные из указанной таблицы.
    /// </summary>
    [OperationContract]
    [WebGet(UriTemplate = "tables/{tableName}/data", ResponseFormat = WebMessageFormat.Json)]
    Task<List<Dictionary<string, object>>> GetTableDataAsync(string tableName);

    /// <summary>
    /// Создает новую таблицу на основе предоставленного определения.
    /// </summary>
    [OperationContract]
    [WebInvoke(Method = "POST", UriTemplate = "tables", RequestFormat = WebMessageFormat.Json, ResponseFormat = WebMessageFormat.Json)]
    [FaultContract(typeof(ErrorDetail))] // Сообщаем клиентам, что в случае ошибки вернется этот тип
    Task CreateTableAsync(TableDefinition tableDef);

    /// <summary>
    /// Вставляет одну строку данных в указанную таблицу.
    /// </summary>
    [OperationContract]
    // UriTemplate теперь тоже упрощается, так как имя таблицы будет в теле запроса
    [WebInvoke(Method = "POST", UriTemplate = "data", RequestFormat = WebMessageFormat.Json, ResponseFormat = WebMessageFormat.Json, BodyStyle = WebMessageBodyStyle.Bare)]
    [FaultContract(typeof(ErrorDetail))]
    // Метод теперь принимает ОДИН параметр
    Task InsertDataAsync(DataInsertionRequest request);
}

#region Data Contracts

[DataContract]
public class KeyValue
{
    [DataMember(Name = "key")]
    public string key { get; set; } = "";

    [DataMember(Name = "value")]
    public object value { get; set; } = null;
}

[DataContract]
public class TableDefinition
{
    // Было: public string TableName { get; set; }
    // Стало: public string tableName { get; set; }
    [DataMember(Name = "tableName")]
    public string tableName { get; set; } = "";

    // Было: public List<ColumnDefinition> Columns { get; set; }
    // Стало: public List<ColumnDefinition> columns { get; set; }
    [DataMember(Name = "columns")]
    public List<ColumnDefinition> columns { get; set; } = new();
}

[DataContract]
public class ColumnDefinition
{
    // Было: public string Name { get; set; }
    // Стало: public string name { get; set; }
    [DataMember(Name = "name")]
    public string name { get; set; } = "";

    // Было: public string Type { get; set; }
    // Стало: public string type { get; set; }
    [DataMember(Name = "type")]
    public string type { get; set; } = "";
}

[DataContract]
public class DataInsertionRequest
{
    [DataMember(Name = "tableName")]
    public string tableName { get; set; } = "";

    // ++ ЗАМЕНЯЕМ Dictionary НА List<KeyValue> ++
    [DataMember(Name = "data")]
    public List<KeyValue> data { get; set; } = new();
}

// ErrorDetail можно оставить как есть, так как мы его только отправляем
[DataContract]
public class ErrorDetail
{
    [DataMember]
    public string ErrorCode { get; set; } = "InternalError";

    [DataMember]
    public string Message { get; set; } = "An unexpected error occurred.";
}

#endregion
