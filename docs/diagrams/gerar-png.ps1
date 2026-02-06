# Gera PNG dos diagramas Mermaid (requer Node.js instalado)
# Uso: .\gerar-png.ps1   ou   powershell -ExecutionPolicy Bypass -File gerar-png.ps1

$ErrorActionPreference = "Stop"
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$root = Split-Path -Parent (Split-Path -Parent $scriptDir)

Set-Location $root

# Tenta encontrar Node.js se nao estiver no PATH (caminhos comuns no Windows)
$nodePaths = @(
    "$env:ProgramFiles\nodejs",
    "${env:ProgramFiles(x86)}\nodejs",
    "$env:LOCALAPPDATA\Programs\node",
    "$env:APPDATA\npm"
)
foreach ($p in $nodePaths) {
    if ($p -and (Test-Path $p)) {
        $env:PATH = "$p;$env:PATH"
    }
}

Write-Host "Verificando Node.js..." -ForegroundColor Cyan
$nodeExe = Get-Command node -ErrorAction SilentlyContinue
if (-not $nodeExe) {
    Write-Host "Node.js nao encontrado." -ForegroundColor Red
    Write-Host "1. Instale em https://nodejs.org/ (versao LTS)" -ForegroundColor Yellow
    Write-Host "2. Feche e abra o terminal (ou o Cursor) de novo" -ForegroundColor Yellow
    Write-Host "3. Execute este script novamente" -ForegroundColor Yellow
    exit 1
}
Write-Host "Node.js $(node --version) encontrado em: $($nodeExe.Source)" -ForegroundColor Green

$diagrams = @(
    @{ in = "docs/diagrams/arquitetura-visao-geral.mmd"; out = "docs/diagrams/arquitetura-visao-geral.png" },
    @{ in = "docs/diagrams/fluxo-comunicacao.mmd"; out = "docs/diagrams/fluxo-comunicacao.png" },
    @{ in = "docs/diagrams/fluxo-autenticacao.mmd"; out = "docs/diagrams/fluxo-autenticacao.png" },
    @{ in = "docs/diagrams/banco-dados-visao.mmd"; out = "docs/diagrams/banco-dados-visao.png" },
    @{ in = "docs/diagrams/fluxo-onboarding.mmd"; out = "docs/diagrams/fluxo-onboarding.png" },
    @{ in = "docs/diagrams/fluxo-agendamento.mmd"; out = "docs/diagrams/fluxo-agendamento.png" },
    @{ in = "docs/diagrams/fluxo-documentos.mmd"; out = "docs/diagrams/fluxo-documentos.png" },
    @{ in = "docs/diagrams/fluxo-motor-alertas.mmd"; out = "docs/diagrams/fluxo-motor-alertas.png" },
    @{ in = "docs/diagrams/fluxo-ciclo-completo.mmd"; out = "docs/diagrams/fluxo-ciclo-completo.png" },
    @{ in = "docs/diagrams/fluxo-cancelamento.mmd"; out = "docs/diagrams/fluxo-cancelamento.png" },
    @{ in = "docs/diagrams/banco-schemas-servicos.mmd"; out = "docs/diagrams/banco-schemas-servicos.png" },
    @{ in = "docs/diagrams/banco-er.mmd"; out = "docs/diagrams/banco-er.png" }
)

foreach ($d in $diagrams) {
    if (-not (Test-Path $d.in)) {
        Write-Host "Arquivo nao encontrado: $($d.in)" -ForegroundColor Yellow
        continue
    }
    Write-Host "Gerando $($d.out)..." -ForegroundColor Cyan
    npx -p @mermaid-js/mermaid-cli mmdc -i $d.in -o $d.out
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  OK: $($d.out)" -ForegroundColor Green
    } else {
        Write-Host "  Erro ao gerar $($d.out)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Concluido. PNGs em docs/diagrams/" -ForegroundColor Green
