// Models/TokenResponse.cs
using System.Text.Json.Serialization;

public class TokenResponse
{
    [JsonPropertyName("token")]
    public string Token { get; set; }
}
