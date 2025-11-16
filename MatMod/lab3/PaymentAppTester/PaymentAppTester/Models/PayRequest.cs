// Models/PayRequest.cs
using System.Text.Json.Serialization;

public class PayRequest
{
    [JsonPropertyName("paymentArtifact")]
    public string PaymentArtifact { get; set; }
}
