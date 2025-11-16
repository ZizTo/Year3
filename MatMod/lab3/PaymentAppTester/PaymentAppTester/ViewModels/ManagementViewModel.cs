// ViewModels/ManagementViewModel.cs
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using PaymentAppTester.Services;
using System;
using System.Text.Json;
using System.Threading.Tasks;

namespace PaymentAppTester.ViewModels
{
    public partial class ManagementViewModel : ObservableObject
    {
        private readonly IApiService _apiService;
        public AppStateService AppState { get; }

        [ObservableProperty]
        [NotifyCanExecuteChangedFor(nameof(GetInfoCommand))]
        [NotifyCanExecuteChangedFor(nameof(ResetTokenCommand))]
        private bool _isBusy;

        public bool IsNotBusy => !IsBusy;

        [ObservableProperty]
        private string _errorMessage;

        [ObservableProperty]
        private string _resultText;

        public ManagementViewModel(IApiService apiService, AppStateService appState)
        {
            _apiService = apiService;
            AppState = appState;
        }

        [RelayCommand(CanExecute = nameof(IsNotBusy))]
        private async Task GetInfoAsync()
        {
            if (string.IsNullOrEmpty(AppState.PaymentArtifact))
            {
                ErrorMessage = "Артефакт не найден. Выставите и оплатите счет.";
                return;
            }

            await ExecuteApiCall(async () =>
            {
                var request = new PaymentInfoRequest { PaymentArtifact = AppState.PaymentArtifact };
                var response = await _apiService.GetPaymentInfoAsync(request);
                ResultText = JsonSerializer.Serialize(response, new JsonSerializerOptions { WriteIndented = true });
            });
        }

        [RelayCommand(CanExecute = nameof(IsNotBusy))]
        private async Task ResetTokenAsync()
        {
            if (string.IsNullOrEmpty(AppState.PaymentArtifact))
            {
                ErrorMessage = "Артефакт не найден. Выставите и оплатите счет.";
                return;
            }

            await ExecuteApiCall(async () =>
            {
                var request = new TokenRequest { PaymentArtifact = AppState.PaymentArtifact };
                var response = await _apiService.ResetTokenAsync(request);
                AppState.Token = response.Token; // Обновляем токен в общем состоянии
                ResultText = $"Новый токен получен: {response.Token}";
            });
        }

        // Вспомогательный метод для уменьшения дублирования кода
        private async Task ExecuteApiCall(Func<Task> apiAction)
        {
            IsBusy = true;
            ErrorMessage = string.Empty;
            ResultText = string.Empty;
            try
            {
                await apiAction();
            }
            catch (ApiException ex) { ErrorMessage = ex.Message; }
            catch (Exception ex) { ErrorMessage = $"Критическая ошибка: {ex.Message}"; }
            finally { IsBusy = false; }
        }
    }
}
