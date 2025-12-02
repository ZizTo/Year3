using CommunityToolkit.Mvvm.ComponentModel;

namespace PaymentAppTester.Services
{
    public partial class AppStateService : ObservableObject
    {
        [ObservableProperty]
        private string _apiBaseUrl;

        [ObservableProperty]
        private string _paymentArtifact;

        [ObservableProperty]
        private string _token;
    }
}
