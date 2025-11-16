// Models/GetBillResponse.cs
using System.Collections.Generic;
using System.Text.Json.Serialization;

public class MethodPrice
{
    [JsonPropertyName("methodName")]
    public string MethodName { get; set; }

    [JsonPropertyName("price")]
    public decimal Price { get; set; }
}

public class GetBillResponse
{
    [JsonPropertyName("serviceName")]
    public string ServiceName { get; set; }

    [JsonPropertyName("methods")]
    public List<MethodPrice> Methods { get; set; }

    [JsonPropertyName("dateFrom")]
    public string DateFrom { get; set; }

    [JsonPropertyName("dateTo")]
    public string DateTo { get; set; }

    [JsonPropertyName("totalPrice")]
    public decimal TotalPrice { get; set; }

    [JsonPropertyName("paymentArtifact")]
    public string PaymentArtifact { get; set; }
}
