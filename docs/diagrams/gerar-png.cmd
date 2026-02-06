@echo off
REM Gera PNG dos diagramas Mermaid (requer Node.js instalado)
REM Duplo-clique ou execute: gerar-png.cmd

cd /d "%~dp0..\.."

where node >nul 2>&1
if errorlevel 1 (
    echo Node.js nao encontrado. Instale em https://nodejs.org/ ^(LTS^) e tente novamente.
    pause
    exit /b 1
)

echo Gerando PNGs...
call npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/arquitetura-visao-geral.mmd -o docs/diagrams/arquitetura-visao-geral.png
call npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/fluxo-comunicacao.mmd -o docs/diagrams/fluxo-comunicacao.png
call npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/fluxo-autenticacao.mmd -o docs/diagrams/fluxo-autenticacao.png
call npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/banco-dados-visao.mmd -o docs/diagrams/banco-dados-visao.png
call npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/fluxo-onboarding.mmd -o docs/diagrams/fluxo-onboarding.png
call npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/fluxo-agendamento.mmd -o docs/diagrams/fluxo-agendamento.png
call npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/fluxo-documentos.mmd -o docs/diagrams/fluxo-documentos.png
call npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/fluxo-motor-alertas.mmd -o docs/diagrams/fluxo-motor-alertas.png
call npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/fluxo-ciclo-completo.mmd -o docs/diagrams/fluxo-ciclo-completo.png
call npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/fluxo-cancelamento.mmd -o docs/diagrams/fluxo-cancelamento.png
call npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/banco-schemas-servicos.mmd -o docs/diagrams/banco-schemas-servicos.png
call npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/banco-er.mmd -o docs/diagrams/banco-er.png

echo.
echo Concluido. PNGs em docs/diagrams/
pause
