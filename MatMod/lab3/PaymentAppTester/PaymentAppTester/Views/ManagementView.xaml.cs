// Views/ManagementView.xaml.cs
using PaymentAppTester.ViewModels;

namespace PaymentAppTester.Views;

public partial class ManagementView : ContentPage
{
    public ManagementView(ManagementViewModel viewModel)
    {
        InitializeComponent();
        BindingContext = viewModel;
    }
}
