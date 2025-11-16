// Services/ApiException.cs
using System;

namespace PaymentAppTester.Services
{
    /// <summary>
    /// Представляет ошибки, которые происходят во время вызова API,
    /// и содержит конкретное сообщение об ошибке от сервера.
    /// </summary>
    public class ApiException : Exception
    {
        public ApiException(string message) : base(message)
        {
        }

        public ApiException(string message, Exception innerException) : base(message, innerException)
        {
        }
    }
}
