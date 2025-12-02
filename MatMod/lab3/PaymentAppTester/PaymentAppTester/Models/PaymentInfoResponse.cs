using System.Collections.Generic;
using System.Text.Json.Serialization;

public class PaymentInfoResponse
{
    [JsonPropertyName("paidUntil")]
    public string PaidUntil { get; set; }

    [JsonPropertyName("availableService")]
    public string AvailableService { get; set; }

    [JsonPropertyName("methods")]
    public List<string> Methods { get; set; }
}
