@echo off
setlocal enabledelayedexpansion
REM Executa testes com cobertura (JaCoCo) em todas as aplicações.
REM Requer Maven instalado. Relatórios em cada modulo: target/site/jacoco/index.html

set "ROOT=%~dp0.."
if not exist "%ROOT%\prenatal-auth" set "ROOT=%~dp0"

echo Cobertura de codigo - Prenatal Digital SUS
echo Modulos: prenatal-auth, prenatal-agenda, prenatal-prontuario, prenatal-documento, prenatal-alertas
echo.

set FAILED=
for %%M in (prenatal-auth prenatal-agenda prenatal-prontuario prenatal-documento prenatal-alertas) do (
    if exist "%ROOT%\%%M" (
        echo [%%M] mvn clean test...
        pushd "%ROOT%\%%M"
        call mvn clean test -q
        if errorlevel 1 (
            set FAILED=!FAILED! %%M
            echo [%%M] FALHOU
        ) else (
            echo [%%M] OK - Relatorio: %%M\target\site\jacoco\index.html
        )
        popd
    ) else (
        echo [SKIP] %%M - pasta nao encontrada
    )
)

echo.
if defined FAILED (
    echo Modulos com falha:%FAILED%
    exit /b 1
)
echo Todos os testes passaram. Relatorios JaCoCo em cada modulo:
echo   prenatal-auth     -^> prenatal-auth/target/site/jacoco/index.html
echo   prenatal-agenda   -^> prenatal-agenda/target/site/jacoco/index.html
echo   prenatal-prontuario -^> prenatal-prontuario/target/site/jacoco/index.html
echo   prenatal-documento -^> prenatal-documento/target/site/jacoco/index.html
echo   prenatal-alertas  -^> prenatal-alertas/target/site/jacoco/index.html
exit /b 0
