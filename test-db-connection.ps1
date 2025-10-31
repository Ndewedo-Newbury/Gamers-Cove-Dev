# Load the PostgreSQL .NET Data Provider
Add-Type -Path "C:\Program Files\PostgreSQL\18\bin\Npgsql.dll"

# Connection string with your credentials
$connString = "Host=localhost;Port=5432;Database=gamerscove;Username=postgres;Password=pass0000"

try {
    $conn = New-Object Npgsql.NpgsqlConnection($connString)
    $conn.Open()
    Write-Host "✅ Database connection successful!" -ForegroundColor Green
    $conn.Close()
} catch {
    Write-Host "❌ Failed to connect to database: $_" -ForegroundColor Red
}
