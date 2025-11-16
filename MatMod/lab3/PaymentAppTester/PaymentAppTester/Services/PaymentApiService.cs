// Services/PaymentApiService.cs
using System;
using System.Net.Http;
using System.Net.Http.Json; // Убедитесь, что установлен NuGet: System.Net.Http.Json
using System.Text.Json;
using System.Threading.Tasks;

namespace PaymentAppTester.Services
{
    public class PaymentApiService : IApiService
    {
        private readonly HttpClient _httpClient;
        private string _baseUrl = "http://localhost"; // Значение по умолчанию

        // Настройки для JsonSerializer, чтобы он не был чувствителен к регистру
        private readonly JsonSerializerOptions _serializerOptions = new()
        {
            PropertyNameCaseInsensitive = true
        };

        public PaymentApiService()
        {
            _httpClient = new HttpClient();
            // Устанавливаем таймаут, чтобы приложение не "висело" вечно
            _httpClient.Timeout = TimeSpan.FromSeconds(30);
        }

        public void SetBaseUrl(string baseUrl)
        {
            if (string.IsNullOrWhiteSpace(baseUrl))
                throw new ArgumentException("Base URL cannot be empty.", nameof(baseUrl));

            _baseUrl = baseUrl.TrimEnd('/');
        }

        public async Task<GetBillResponse> GetBillAsync(GetBillRequest request)
        {
            var uri = new Uri($"{_baseUrl}/bill");
            return await PostAsync<GetBillRequest, GetBillResponse>(uri, request);
        }

        public async Task<PayResponse> PayAsync(PayRequest request)
        {
            var uri = new Uri($"{_baseUrl}/pay");
            return await PostAsync<PayRequest, PayResponse>(uri, request);
        }

        public async Task<PaymentInfoResponse> GetPaymentInfoAsync(PaymentInfoRequest request)
        {
            var uri = new Uri($"{_baseUrl}/payment/info");
            return await PostAsync<PaymentInfoRequest, PaymentInfoResponse>(uri, request);
        }

        public async Task<TokenResponse> ResetTokenAsync(TokenRequest request)
        {
            var uri = new Uri($"{_baseUrl}/token/reset");
            return await PostAsync<TokenRequest, TokenResponse>(uri, request);
        }

        /// <summary>
        /// Универсальный метод для выполнения POST-запросов и обработки ответов.
        /// </summary>
        /// <typeparam name="TRequest">Тип объекта запроса</typeparam>
        /// <typeparam name="TResponse">Тип ожидаемого объекта ответа</typeparam>
        /// <param name="uri">Конечный URI эндпоинта</param>
        /// <param name="data">Объект запроса для отправки</param>
        /// <returns>Десериализованный объект ответа</returns>
        private async Task<TResponse> PostAsync<TRequest, TResponse>(Uri uri, TRequest data)
        {
            try
            {
                // Отправляем POST-запрос с JSON-телом
                HttpResponseMessage response = await _httpClient.PostAsJsonAsync(uri, data, _serializerOptions);

                // Этот метод выбросит исключение HttpRequestException, если код ответа не 2xx (например, 404, 500)
                response.EnsureSuccessStatusCode();

                // Если все успешно, читаем и десериализуем успешный ответ
                return await response.Content.ReadFromJsonAsync<TResponse>(_serializerOptions);
            }
            catch (HttpRequestException ex)
            {
                // Это самая важная часть: обработка ошибок сети или сервера.
                // ex.StatusCode содержит код ответа (например, 400, 404, 500)
                string errorMessage = ex.Message;

                // Пытаемся извлечь наше кастомное сообщение об ошибке из тела ответа
                if (ex.StatusCode.HasValue)
                {
                    try
                    {
                        // Пытаемся десериализовать тело ошибки в нашу модель Error
                        var errorResponse = JsonSerializer.Deserialize<Error>(ex.Message, _serializerOptions);
                        if (errorResponse != null && !string.IsNullOrEmpty(errorResponse.Description))
                        {
                            errorMessage = errorResponse.Description;
                        }
                    }
                    catch
                    {
                        // Если тело ответа не является JSON'ом или имеет другую структуру,
                        // оставляем стандартное сообщение об ошибке.
                        errorMessage = $"Server returned status code {ex.StatusCode} but the error response could not be parsed.";
                    }
                }

                // "Пробрасываем" наверх наше кастомное исключение с понятным текстом
                throw new ApiException(errorMessage, ex);
            }
            catch (Exception ex)
            {
                // Обработка других возможных ошибок (например, нет интернета, таймаут)
                throw new ApiException($"An unexpected error occurred: {ex.Message}", ex);
            }
        }
    }
}
