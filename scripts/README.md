# Scripts – Cobertura de Código (JaCoCo)

Scripts para executar testes com cobertura em todas as aplicações do projeto. Cada módulo já possui **JaCoCo** configurado no `pom.xml`; os relatórios são gerados ao rodar os testes.

---

## Requisito

- **Maven** instalado e disponível no `PATH`.

---

## Rodar cobertura em todas as aplicações

Os scripts executam `mvn clean test` em cada módulo (prenatal-auth, prenatal-agenda, prenatal-prontuario, prenatal-documento, prenatal-alertas). O JaCoCo gera o relatório automaticamente na fase `test`.

### Da raiz do projeto

**PowerShell:**
```powershell
.\scripts\coverage.ps1
```

**CMD:**
```cmd
scripts\coverage.cmd
```

### De dentro da pasta `scripts`

**PowerShell:**
```powershell
.\coverage.ps1
```

**CMD:**
```cmd
coverage.cmd
```

Se algum módulo falhar nos testes, o script exibe quais falharam e encerra com código de erro.

---

## Rodar cobertura em um único módulo

Na **raiz do projeto**, entre na pasta do módulo e rode:

```bash
cd prenatal-auth   # ou prenatal-agenda, prenatal-prontuario, prenatal-documento, prenatal-alertas
mvn clean test
```

---

## Onde ver o relatório

Os relatórios HTML ficam em cada módulo, em `target/site/jacoco/index.html`:

| Módulo             | Caminho do relatório (a partir da raiz do projeto)        |
|--------------------|------------------------------------------------------------|
| prenatal-auth      | `prenatal-auth/target/site/jacoco/index.html`             |
| prenatal-agenda    | `prenatal-agenda/target/site/jacoco/index.html`           |
| prenatal-prontuario| `prenatal-prontuario/target/site/jacoco/index.html`       |
| prenatal-documento | `prenatal-documento/target/site/jacoco/index.html`        |
| prenatal-alertas   | `prenatal-alertas/target/site/jacoco/index.html`          |

Abra o `index.html` no navegador para ver cobertura de instruções, ramos e linhas por pacote. A configuração do JaCoCo (exclusões de config, DTOs, entities) está no `pom.xml` de cada módulo. Relatórios visuais (imagens) por projeto estão em [docs/coverage](../docs/coverage/).

---

## Arquivos nesta pasta

| Arquivo        | Descrição                                                                 |
|----------------|---------------------------------------------------------------------------|
| `coverage.ps1` | Script PowerShell: executa `mvn clean test` em todos os módulos listados. |
| `coverage.cmd` | Script CMD (Windows): mesma função, para uso em Prompt de Comando.        |

A seção [Cobertura de Código](../README.md#-cobertura-de-código) do [README principal](../README.md) também descreve como rodar a cobertura.
