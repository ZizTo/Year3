using DatabaseGrpcService.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddCors(o => o.AddDefaultPolicy(builder =>
{
    builder.WithOrigins("http://localhost:5202")
           .AllowAnyMethod()
           .AllowAnyHeader()
           .WithExposedHeaders("Grpc-Status", "Grpc-Message");
}));

builder.Services.AddGrpc();

var app = builder.Build();

app.UseCors();

app.UseGrpcWeb();

app.MapGrpcService<DatabaseManagerService>().EnableGrpcWeb();

app.Run();
