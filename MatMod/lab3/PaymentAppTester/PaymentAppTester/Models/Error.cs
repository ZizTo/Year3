using System.Text.Json.Serialization;

public class Error
{
    [JsonPropertyName("description")]
    public string Description { get; set; }
}
