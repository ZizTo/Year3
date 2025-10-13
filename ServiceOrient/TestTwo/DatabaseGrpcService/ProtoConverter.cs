using Google.Protobuf.WellKnownTypes;
using System.Data;
using System.Globalization;

namespace DatabaseGrpcService.Services; // Убедитесь, что namespace совпадает с вашим проектом

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

            // ++ ИЗМЕНЕНИЕ ЗДЕСЬ ++
            case string s:
                // Пытаемся распознать, не является ли строка датой в формате HTML-инпута
                if (DateTime.TryParse(s, CultureInfo.InvariantCulture, DateTimeStyles.None, out var dtFromString))
                {
                    // Если получилось - форматируем в универсальный стандарт, понятный SQL
                    return Value.ForString(dtFromString.ToUniversalTime().ToString("o"));
                }
                // Если это не дата, просто возвращаем строку
                return Value.ForString(s);

            case DateTime dt:
                // Если тип уже DateTime, сразу форматируем правильно
                return Value.ForString(dt.ToUniversalTime().ToString("o"));

            default:
                return Value.ForString(value.ToString());
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
            // Для сложных типов (Struct, List) можно добавить логику здесь, но для нашей задачи этого достаточно.
            _ => null
        };
    }
}
