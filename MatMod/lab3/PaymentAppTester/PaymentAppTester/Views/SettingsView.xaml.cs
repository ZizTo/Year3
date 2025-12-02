using PaymentAppTester.ViewModels;

namespace PaymentAppTester.Views;

public partial class SettingsView : ContentPage
{
    public SettingsView(SettingsViewModel viewModel)
    {
        InitializeComponent();

        BindingContext = viewModel;
    }
}
