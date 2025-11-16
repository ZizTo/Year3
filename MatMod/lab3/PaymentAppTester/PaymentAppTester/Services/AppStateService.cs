// Services/AppStateService.cs
using CommunityToolkit.Mvvm.ComponentModel;

namespace PaymentAppTester.Services
{
    // Partial class для генерации кода CommunityToolkit.Mvvm
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
