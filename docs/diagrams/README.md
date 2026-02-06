# Diagramas Mermaid – Exportar para PNG

Os diagramas do README principal estão em código Mermaid. Você pode gerar arquivos PNG (ou SVG) de duas formas:

---

## Opção 1: Mermaid Live Editor (sem instalar nada)

1. Acesse **[https://mermaid.live](https://mermaid.live)**.
2. Copie o conteúdo de um arquivo `.mmd` desta pasta e cole no editor.
3. O diagrama será renderizado à direita.
4. Use **Actions → PNG** ou **Actions → SVG** para baixar a imagem.

---

## Opção 2: Mermaid CLI (linha de comando)

Requer **Node.js** instalado: https://nodejs.org/ (versão LTS).

### Gerar todos os PNG (script pronto)

Na **raiz do projeto**, execute:

**Windows – duplo-clique ou CMD:**
```cmd
docs\diagrams\gerar-png.cmd
```

**Windows – PowerShell:**
```powershell
.\docs\diagrams\gerar-png.ps1
```

Os PNGs serão criados em `docs/diagrams/`.

### Gerar um PNG manualmente

```bash
npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/arquitetura-visao-geral.mmd -o docs/diagrams/arquitetura-visao-geral.png
```

### Tamanho da imagem (escala)

Para PNG maior (melhor para impressão/apresentação):

```bash
npx -p @mermaid-js/mermaid-cli mmdc -i docs/diagrams/arquitetura-visao-geral.mmd -o arquitetura.png -s 2
```

O `-s 2` dobra a escala (2x). Use `-s 3` para triplicar.

---

## Arquivos .mmd nesta pasta

| Arquivo | Descrição |
|---------|-----------|
| `arquitetura-visao-geral.mmd` | Visão geral dos serviços e infraestrutura |
| `fluxo-comunicacao.mmd` | Fluxo de comunicação e dependências (Docker) |
| `fluxo-autenticacao.mmd` | Sequência de autenticação OAuth2/JWT |
| `banco-dados-visao.mmd` | Visão da arquitetura de dados (schemas) |
| `fluxo-onboarding.mmd` | Onboarding e cadastro inicial |
| `fluxo-agendamento.mmd` | Agendamento de consulta |
| `fluxo-documentos.mmd` | Upload e gestão de documentos |
| `fluxo-motor-alertas.mmd` | Motor de alertas (prenatal-alertas) |
| `fluxo-ciclo-completo.mmd` | Ciclo completo do pré-natal |
| `fluxo-cancelamento.mmd` | Cancelamento de consulta |
| `banco-schemas-servicos.mmd` | Schemas e serviços (quem usa cada schema) |
| `banco-er.mmd` | Diagrama entidade-relacionamento (tabelas e relacionamentos) |

O [README principal](../../README.md) usa **somente** as imagens PNG desta pasta para exibir os diagramas.
