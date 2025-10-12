// Program.cs (в проекте DatabaseClientWebApp)

var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

// Настраиваем отдачу файлов по умолчанию (например, index.html)
app.UseDefaultFiles();

// Включаем возможность отдавать статичные файлы из папки wwwroot
app.UseStaticFiles();

app.Run();
