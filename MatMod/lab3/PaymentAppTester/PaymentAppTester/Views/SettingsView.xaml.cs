// Views/SettingsView.xaml.cs
using PaymentAppTester.ViewModels;

namespace PaymentAppTester.Views;

// Ключевое слово "partial" говорит, что этот класс является частью
// определения, которое будет дополнено сгенерированным кодом (...sg.cs)
public partial class SettingsView : ContentPage
{
    public SettingsView(SettingsViewModel viewModel)
    {
        // Этот метод связывает XAML и сгенерированный код
        InitializeComponent();

        // А эта строка связывает XAML с нашей ViewModel
        BindingContext = viewModel;
    }
}
