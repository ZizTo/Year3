using Google.Protobuf.WellKnownTypes;
using System.Data;

namespace DatabaseGrpcService.Services;

public static class ProtoConverter
{
    // C# object to Google.Protobuf.WellKnownTypes.Value.
    public static Value ToValue(object? value)
    {
        if (value is null || value is DBNull)
        {
            return Value.ForNull();
        }

        switch (value)
        {
            case bool b: return Value.ForBool(b);
            case double d: return Value.ForNumber(d);
            case float f: return Value.ForNumber(f);
            case int i: return Value.ForNumber(i);
            case long l: return Value.ForNumber(l);
            case decimal m: return Value.ForNumber((double)m);
            case string s: return Value.ForString(s);
            case DateTime dt: return Value.ForString(dt.ToUniversalTime().ToString("o"));
            default: return Value.ForString(value.ToString());
        }
    }

    public static object? ToObject(Value value)
    {
        return value.KindCase switch
        {
            Value.KindOneofCase.NullValue => null,
            Value.KindOneofCase.NumberValue => value.NumberValue,
            Value.KindOneofCase.StringValue => value.StringValue,
            Value.KindOneofCase.BoolValue => value.BoolValue,
            _ => null
        };
    }
}
