using CoreWCF;
using CoreWCF.Configuration;
using CoreWCF.Description;
using CoreWCF.Web;
using DatabaseWcfService;

var builder = WebApplication.CreateBuilder(args);

const string myAllowSpecificOrigins = "_myAllowSpecificOrigins";

builder.Services.AddCors(options =>
{
    options.AddPolicy(name: myAllowSpecificOrigins,
                      policy =>
                      {
                          policy.WithOrigins("http://localhost:5023")
                                .AllowAnyHeader()
                                .AllowAnyMethod();
                      });
});

builder.Services.AddServiceModelServices();
builder.Services.AddServiceModelMetadata();
builder.Services.AddServiceModelWebServices();
builder.Services.AddTransient<DatabaseService>();

var app = builder.Build();

app.UseRouting();

app.UseCors(myAllowSpecificOrigins);

app.UseServiceModel(serviceBuilder =>
{
    serviceBuilder.AddService<DatabaseService>(options =>
    {
        options.DebugBehavior.IncludeExceptionDetailInFaults = true;
    });

    serviceBuilder.AddServiceEndpoint<DatabaseService, IDatabaseService>(new WebHttpBinding(), "/rest");
    serviceBuilder.AddServiceEndpoint<DatabaseService, IDatabaseService>(new BasicHttpBinding(), "/soap");

    var serviceMetadataBehavior = app.Services.GetRequiredService<ServiceMetadataBehavior>();
    serviceMetadataBehavior.HttpGetEnabled = true;
});

app.Run();
