// ViewModels/BillingViewModel.cs
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using PaymentAppTester.Services;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace PaymentAppTester.ViewModels
{
    public partial class BillingViewModel : ObservableObject
    {
        private readonly IApiService _apiService;
        private readonly AppStateService _appState;

        [ObservableProperty]
        [NotifyCanExecuteChangedFor(nameof(GetBillCommand))]
        [NotifyCanExecuteChangedFor(nameof(PayCommand))]
        private bool _isBusy;

        public bool IsNotBusy => !IsBusy;

        [ObservableProperty]
        private string _errorMessage;

        // --- Свойства для выставления счета ---
        [ObservableProperty] private string _serviceName = "Online marketplace";
        [ObservableProperty] private string _methodsText = "getInfo,getPrice";
        [ObservableProperty] private DateTime _dateFrom = DateTime.Today;
        [ObservableProperty] private DateTime _dateTo = DateTime.Today.AddDays(12);

        // --- Свойства для результата счета ---
        [ObservableProperty][NotifyPropertyChangedFor(nameof(IsBillReady))] private string _paymentArtifact;
        [ObservableProperty] private decimal _totalPrice;
        public bool IsBillReady => !string.IsNullOrEmpty(PaymentArtifact);

        // --- Свойства для результата оплаты ---
        [ObservableProperty][NotifyPropertyChangedFor(nameof(IsPaid))] private string _token;
        public bool IsPaid => !string.IsNullOrEmpty(Token);

        public BillingViewModel(IApiService apiService, AppStateService appState)
        {
            _apiService = apiService;
            _appState = appState;
        }

        [RelayCommand(CanExecute = nameof(IsNotBusy))]
        private async Task GetBillAsync()
        {
            IsBusy = true;
            ErrorMessage = string.Empty;
            PaymentArtifact = null; // Сбрасываем предыдущий результат
            Token = null;

            try
            {
                var request = new GetBillRequest
                {
                    ServiceName = this.ServiceName,
                    Methods = this.MethodsText.Split(',').Select(m => m.Trim()).ToList(),
                    DateFrom = this.DateFrom.ToString("yyyy-MM-dd"),
                    DateTo = this.DateTo.ToString("yyyy-MM-dd")
                };

                var response = await _apiService.GetBillAsync(request);

                PaymentArtifact = response.PaymentArtifact;
                TotalPrice = response.TotalPrice;
                _appState.PaymentArtifact = response.PaymentArtifact; // Сохраняем в общем состоянии
            }
            catch (ApiException ex) { ErrorMessage = ex.Message; }
            catch (Exception ex) { ErrorMessage = $"Критическая ошибка: {ex.Message}"; }
            finally { IsBusy = false; }
        }

        [RelayCommand(CanExecute = nameof(IsNotBusy))]
        private async Task PayAsync()
        {
            if (!IsBillReady)
            {
                ErrorMessage = "Сначала необходимо выставить счет.";
                return;
            }

            IsBusy = true;
            ErrorMessage = string.Empty;
            Token = null;

            try
            {
                var request = new PayRequest { PaymentArtifact = this.PaymentArtifact };
                var response = await _apiService.PayAsync(request);
                Token = response.Token;
                _appState.Token = response.Token; // Сохраняем в общем состоянии
            }
            catch (ApiException ex) { ErrorMessage = ex.Message; }
            catch (Exception ex) { ErrorMessage = $"Критическая ошибка: {ex.Message}"; }
            finally { IsBusy = false; }
        }
    }
}
    