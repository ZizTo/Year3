using Google.Protobuf.WellKnownTypes;
using System.Data;

namespace DatabaseGrpcService; // Убедитесь, что namespace совпадает с вашим проектом

public static class ProtoConverter
{
    /// <summary>
    /// Преобразует стандартный C# object в Google.Protobuf.WellKnownTypes.Value.
    /// </summary>
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
            // Даты преобразуем в стандартный ISO 8601 формат
            case DateTime dt: return Value.ForString(dt.ToUniversalTime().ToString("o"));
            // Для всех остальных типов (Guid и т.д.) просто возвращаем их строковое представление
            default: return Value.ForString(value.ToString());
        }
    }
}
