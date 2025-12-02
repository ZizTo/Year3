using System.Collections.Generic;
using System.Text.Json.Serialization;

public class GetBillRequest
{
    [JsonPropertyName("serviceName")]
    public string ServiceName { get; set; }

    [JsonPropertyName("methods")]
    public List<string> Methods { get; set; }

    [JsonPropertyName("dateFrom")]
    public string DateFrom { get; set; }

    [JsonPropertyName("dateTo")]
    public string DateTo { get; set; }
}
