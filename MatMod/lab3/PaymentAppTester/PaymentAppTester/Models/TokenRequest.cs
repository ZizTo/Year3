using System.Text.Json.Serialization;

public class TokenRequest
{
    [JsonPropertyName("paymentArtifact")]
    public string PaymentArtifact { get; set; }
}
