// Services/IApiService.cs
using System.Threading.Tasks;

namespace PaymentAppTester.Services
{
    public interface IApiService
    {
        /// <summary>
        /// Устанавливает базовый URL для всех запросов к API.
        /// </summary>
        /// <param name="baseUrl">Адрес API, например, "http://192.168.1.100:5000"</param>
        void SetBaseUrl(string baseUrl);

        /// <summary>
        /// Отправляет запрос на выставление счета.
        /// </summary>
        Task<GetBillResponse> GetBillAsync(GetBillRequest request);

        /// <summary>
        /// Отправляет запрос на оплату счета.
        /// </summary>
        Task<PayResponse> PayAsync(PayRequest request);

        /// <summary>
        /// Отправляет запрос на получение информации об оплате.
        /// </summary>
        Task<PaymentInfoResponse> GetPaymentInfoAsync(PaymentInfoRequest request);

        /// <summary>
        /// Отправляет запрос на сброс/обновление токена.
        /// </summary>
        Task<TokenResponse> ResetTokenAsync(TokenRequest request);
    }
}
