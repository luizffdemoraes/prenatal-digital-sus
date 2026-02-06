# Executa testes com cobertura (JaCoCo) em todas as aplicações.
# Requer Maven instalado. Relatórios em cada modulo: target/site/jacoco/index.html

$ErrorActionPreference = "Stop"
# Raiz do projeto = pasta que contem "scripts" e os modulos (prenatal-*)
$root = Split-Path -Parent $PSScriptRoot
$modules = @("prenatal-auth", "prenatal-agenda", "prenatal-prontuario", "prenatal-documento", "prenatal-alertas")

Write-Host "Cobertura de codigo - Prenatal Digital SUS" -ForegroundColor Cyan
Write-Host "Modulos: $($modules -join ', ')" -ForegroundColor Gray
Write-Host ""

$failed = @()
foreach ($mod in $modules) {
    $dir = Join-Path $root $mod
    if (-not (Test-Path $dir)) {
        Write-Host "[SKIP] $mod - pasta nao encontrada" -ForegroundColor Yellow
        continue
    }
    Write-Host "[$mod] mvn clean test..." -ForegroundColor Cyan
    Push-Location $dir
    try {
        & mvn clean test -q
        if ($LASTEXITCODE -ne 0) {
            $failed += $mod
            Write-Host "[$mod] FALHOU" -ForegroundColor Red
        } else {
            $report = Join-Path $dir "target\site\jacoco\index.html"
            Write-Host "[$mod] OK - Relatorio: $report" -ForegroundColor Green
        }
    } finally {
        Pop-Location
    }
}

Write-Host ""
if ($failed.Count -gt 0) {
    Write-Host "Modulos com falha: $($failed -join ', ')" -ForegroundColor Red
    exit 1
}
Write-Host "Todos os testes passaram. Relatorios JaCoCo em cada modulo:" -ForegroundColor Green
foreach ($mod in $modules) {
    Write-Host "  $mod -> $mod/target/site/jacoco/index.html"
}
exit 0
