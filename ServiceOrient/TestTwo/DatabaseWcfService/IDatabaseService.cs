using CoreWCF;
using CoreWCF.Web;
using System.Runtime.Serialization;

namespace DatabaseWcfService;

[ServiceContract]
public interface IDatabaseService
{
    [OperationContract]
    [WebGet(UriTemplate = "tables", ResponseFormat = WebMessageFormat.Json)]
    Task<List<string>> GetTablesAsync();

    [OperationContract]
    [WebGet(UriTemplate = "tables/{tableName}/schema", ResponseFormat = WebMessageFormat.Json)]
    Task<List<ColumnDefinition>> GetTableSchemaAsync(string tableName);

    [OperationContract]
    [WebGet(UriTemplate = "tables/{tableName}/data", ResponseFormat = WebMessageFormat.Json)]
    Task<List<Dictionary<string, object>>> GetTableDataAsync(string tableName);

    [OperationContract]
    [WebInvoke(Method = "POST", UriTemplate = "tables", RequestFormat = WebMessageFormat.Json, ResponseFormat = WebMessageFormat.Json)]
    [FaultContract(typeof(ErrorDetail))]
    Task CreateTableAsync(TableDefinition tableDef);


    [OperationContract]
    [WebInvoke(Method = "POST", UriTemplate = "data", RequestFormat = WebMessageFormat.Json, ResponseFormat = WebMessageFormat.Json, BodyStyle = WebMessageBodyStyle.Bare)]
    [FaultContract(typeof(ErrorDetail))]
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
    [DataMember(Name = "tableName")]
    public string tableName { get; set; } = "";

    [DataMember(Name = "columns")]
    public List<ColumnDefinition> columns { get; set; } = new();
}

[DataContract]
public class ColumnDefinition
{
    [DataMember(Name = "name")]
    public string name { get; set; } = "";

    [DataMember(Name = "type")]
    public string type { get; set; } = "";
}

[DataContract]
public class DataInsertionRequest
{
    [DataMember(Name = "tableName")]
    public string tableName { get; set; } = "";

    [DataMember(Name = "data")]
    public List<KeyValue> data { get; set; } = new();
}

[DataContract]
public class ErrorDetail
{
    [DataMember]
    public string ErrorCode { get; set; } = "InternalError";

    [DataMember]
    public string Message { get; set; } = "An unexpected error occurred.";
}

#endregion
