using System.Threading.Tasks;

namespace PaymentAppTester.Services
{
    public interface IApiService
    {
        void SetBaseUrl(string baseUrl);

        Task<GetBillResponse> GetBillAsync(GetBillRequest request);

        Task<PayResponse> PayAsync(PayRequest request);

        Task<PaymentInfoResponse> GetPaymentInfoAsync(PaymentInfoRequest request);

        Task<TokenResponse> ResetTokenAsync(TokenRequest request);
    }
}
