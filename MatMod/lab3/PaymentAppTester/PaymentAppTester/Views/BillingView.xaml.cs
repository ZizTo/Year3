using PaymentAppTester.ViewModels;

namespace PaymentAppTester.Views;

public partial class BillingView : ContentPage
{
    public BillingView(BillingViewModel viewModel)
    {
        InitializeComponent();
        BindingContext = viewModel;
    }
}
