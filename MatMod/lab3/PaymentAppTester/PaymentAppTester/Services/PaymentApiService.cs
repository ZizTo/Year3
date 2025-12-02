using System;
using System.Net.Http;
using System.Net.Http.Json; 
using System.Text.Json;
using System.Threading.Tasks;

namespace PaymentAppTester.Services
{
    public class PaymentApiService : IApiService
    {
        private readonly HttpClient _httpClient;
        private string _baseUrl = "http://localhost";

        private readonly JsonSerializerOptions _serializerOptions = new()
        {
            PropertyNameCaseInsensitive = true
        };

        public PaymentApiService()
        {
            _httpClient = new HttpClient();
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

        private async Task<TResponse> PostAsync<TRequest, TResponse>(Uri uri, TRequest data)
        {
            try
            {
                HttpResponseMessage response = await _httpClient.PostAsJsonAsync(uri, data, _serializerOptions);

                response.EnsureSuccessStatusCode();

                return await response.Content.ReadFromJsonAsync<TResponse>(_serializerOptions);
            }
            catch (HttpRequestException ex)
            {
                string errorMessage = ex.Message;

                if (ex.StatusCode.HasValue)
                {
                    try
                    {
                        var errorResponse = JsonSerializer.Deserialize<Error>(ex.Message, _serializerOptions);
                        if (errorResponse != null && !string.IsNullOrEmpty(errorResponse.Description))
                        {
                            errorMessage = errorResponse.Description;
                        }
                    }
                    catch
                    {
                        errorMessage = $"Server returned status code {ex.StatusCode} but the error response could not be parsed.";
                    }
                }

                throw new ApiException(errorMessage, ex);
            }
            catch (Exception ex)
            {
                throw new ApiException($"An unexpected error occurred: {ex.Message}", ex);
            }
        }
    }
}
