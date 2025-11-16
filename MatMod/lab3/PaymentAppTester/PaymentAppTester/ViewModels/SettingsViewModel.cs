// ViewModels/SettingsViewModel.cs
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using PaymentAppTester.Services;
using Microsoft.Maui.Graphics;

namespace PaymentAppTester.ViewModels
{
    public partial class SettingsViewModel : ObservableObject
    {
        private readonly IApiService _apiService;
        private readonly AppStateService _appState;

        [ObservableProperty]
        private string _apiUrl;

        [ObservableProperty]
        private string _statusMessage;

        [ObservableProperty]
        private Color _statusColor;

        public SettingsViewModel(IApiService apiService, AppStateService appState)
        {
            _apiService = apiService;
            _appState = appState;
            // Загружаем сохраненный URL при запуске
            ApiUrl = _appState.ApiBaseUrl ?? "http://10.0.2.2:5000";
        }

        [RelayCommand]
        private void Save()
        {
            if (string.IsNullOrWhiteSpace(ApiUrl))
            {
                StatusColor = Colors.Red;
                StatusMessage = "Адрес не может быть пустым!";
                return;
            }

            try
            {
                _apiService.SetBaseUrl(ApiUrl);
                _appState.ApiBaseUrl = ApiUrl; // Сохраняем в общем состоянии
                StatusColor = Colors.Green;
                StatusMessage = "Адрес сохранен успешно!";
            }
            catch (Exception ex)
            {
                StatusColor = Colors.Red;
                StatusMessage = $"Ошибка: {ex.Message}";
            }
        }
    }
}
