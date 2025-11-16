// Models/PaymentInfoRequest.cs
using System.Text.Json.Serialization;

public class PaymentInfoRequest
{
    [JsonPropertyName("paymentArtifact")]
    public string PaymentArtifact { get; set; }
}
