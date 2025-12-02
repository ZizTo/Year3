using System.Text.Json.Serialization;

public class PayResponse
{
    [JsonPropertyName("message")]
    public string Message { get; set; }

    [JsonPropertyName("token")]
    public string Token { get; set; }
}
