using PaymentAppTester.Services;
using PaymentAppTester.ViewModels;
using PaymentAppTester.Views;

namespace PaymentAppTester;

public static class MauiProgram
{
    public static MauiApp CreateMauiApp()
    {
        var builder = MauiApp.CreateBuilder();
        builder
            .UseMauiApp<App>()
            .ConfigureFonts(fonts =>
            {
                fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
                fonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
            });

        builder.Services.AddSingleton<IApiService, PaymentApiService>();
        builder.Services.AddSingleton<AppStateService>();

        builder.Services.AddTransient<SettingsViewModel>();
        builder.Services.AddTransient<SettingsView>();

        builder.Services.AddTransient<BillingViewModel>();
        builder.Services.AddTransient<BillingView>();

        builder.Services.AddTransient<ManagementViewModel>();
        builder.Services.AddTransient<ManagementView>();

        return builder.Build();
    }
}
