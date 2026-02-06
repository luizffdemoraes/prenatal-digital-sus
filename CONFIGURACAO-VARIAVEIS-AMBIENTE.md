# 🔧 Configuração de Variáveis de Ambiente no Windows

Este guia explica como configurar as variáveis de ambiente necessárias **nas variáveis de ambiente da máquina** (Windows).

**Regra do projeto:** as variáveis **devem estar definidas nas variáveis de ambiente da máquina**. Ao subir os serviços com `docker compose up`, o Docker Compose **obtém** os valores das variáveis de ambiente definidas na máquina e repassa aos containers. Não é usado arquivo `.env` no projeto.

---

## 📑 Índice

1. [Início Rápido](#-início-rápido)
2. [Variáveis Necessárias](#-variáveis-necessárias)
3. [Como Gerar Senha de App do Gmail](#-como-gerar-senha-de-app-do-gmail-passo-a-passo-completo)
4. [Método 1: Via Interface Gráfica do Windows](#-método-1-via-interface-gráfica-do-windows-recomendado---permanente)
5. [Método 2: Via PowerShell (Temporário)](#-método-2-via-powershell-temporário---apenas-na-sessão-atual)
6. [Método 3: Via PowerShell (Permanente)](#-método-3-via-powershell-permanente---adiciona-ao-sistema)
7. [Como Verificar se Estão Configuradas](#-como-verificar-se-estão-configuradas)
8. [Testando a Configuração Completa](#-testando-a-configuração-completa)
9. [Troubleshooting (Solução de Problemas)](#-troubleshooting-solução-de-problemas)
10. [Como o Docker Compose Obtém as Variáveis da Máquina](#-como-o-docker-compose-obtém-as-variáveis-da-máquina)

---

## 🚀 Início Rápido

**Para começar rapidamente, siga esta ordem:**

1. **Primeiro:** Gere a senha de app do Gmail → [Como Gerar Senha de App do Gmail](#-como-gerar-senha-de-app-do-gmail-passo-a-passo-completo)
2. **Depois:** Configure as variáveis no Windows → [Método 1: Via Interface Gráfica](#-método-1-via-interface-gráfica-do-windows-recomendado---permanente)
3. **Por fim:** Verifique se funcionou → [Como Verificar](#-como-verificar-se-estão-configuradas)

---

## 📋 Variáveis Necessárias

### Obrigatórias para envio de e-mails (prenatal-alertas):

- **`SMTP_EMAIL`** - Seu e-mail SMTP (ex: `seu-email@gmail.com`)
- **`SMTP_PASSWORD`** - Senha ou senha de app do e-mail

### Opcionais (têm valores padrão no docker-compose.yml):

- **`POSTGRES_PASSWORD`** - Senha do PostgreSQL (padrão: `password`)
- **`CLIENT_ID`** - ID do cliente OAuth2 (padrão: `myclientid`)
- **`CLIENT_SECRET`** - Secret do cliente OAuth2 (padrão: `myclientsecret`)

---

## 🪟 Método 1: Via Interface Gráfica do Windows (Recomendado - Permanente)

### Passo a passo detalhado:

#### Passo 1: Abrir as Propriedades do Sistema

**Opção A - Atalho de teclado:**
1. Pressione as teclas `Windows + R` simultaneamente
2. Uma janela "Executar" aparecerá
3. Digite exatamente: `sysdm.cpl`
4. Pressione `Enter` ou clique em "OK"

**Opção B - Menu Iniciar:**
1. Clique com o botão direito no ícone "Este Computador" (ou "Meu Computador") na área de trabalho ou no Explorador de Arquivos
2. Selecione "Propriedades"
3. No painel esquerdo, clique em "Configurações avançadas do sistema"

**Opção C - Painel de Controle:**
1. Abra o "Painel de Controle"
2. Vá em "Sistema e Segurança" → "Sistema"
3. No painel esquerdo, clique em "Configurações avançadas do sistema"

#### Passo 2: Acessar Variáveis de Ambiente

1. Na janela "Propriedades do Sistema" que abriu, você verá várias abas no topo
2. Clique na aba **"Avançado"**
3. Na seção "Variáveis de ambiente", clique no botão **"Variáveis de Ambiente..."**
4. Uma nova janela "Variáveis de Ambiente" abrirá

#### Passo 3: Adicionar a Variável SMTP_EMAIL

1. Na janela "Variáveis de Ambiente", você verá duas seções:
   - **Variáveis de usuário para [seu usuário]** (parte superior)
   - **Variáveis do sistema** (parte inferior)

2. **Recomendação:** Use "Variáveis de usuário" (só para seu usuário) ou "Variáveis do sistema" (para todos os usuários)

3. Na seção **"Variáveis de usuário"**, clique no botão **"Novo..."**

4. Uma janela "Nova Variável de Usuário" aparecerá com dois campos:
   - **Nome da variável:** Digite exatamente: `SMTP_EMAIL`
   - **Valor da variável:** Digite seu e-mail completo, por exemplo: `seu-email@gmail.com`

5. Clique em **"OK"** para salvar

#### Passo 4: Adicionar a Variável SMTP_PASSWORD

1. Ainda na janela "Variáveis de Ambiente", na seção "Variáveis de usuário", clique novamente em **"Novo..."**

2. Na janela "Nova Variável de Usuário":
   - **Nome da variável:** Digite exatamente: `SMTP_PASSWORD`
   - **Valor da variável:** Digite a senha de app do Gmail que você gerou (veja seção "Como Gerar Senha de App do Gmail" abaixo)
     - **IMPORTANTE:** Use a senha de app de 16 caracteres gerada no Gmail, NÃO a senha normal da sua conta

3. Clique em **"OK"** para salvar

#### Passo 5: Verificar e Finalizar

1. Na janela "Variáveis de Ambiente", você deve ver ambas as variáveis listadas na seção "Variáveis de usuário":
   - `SMTP_EMAIL` com o valor do seu e-mail
   - `SMTP_PASSWORD` com o valor da senha de app

2. Clique em **"OK"** na janela "Variáveis de Ambiente"

3. Clique em **"OK"** na janela "Propriedades do Sistema"

4. **IMPORTANTE:** Feche completamente o PowerShell ou CMD que você está usando

5. Abra um novo PowerShell ou CMD para que as variáveis sejam carregadas

#### Passo 6: Verificar se Funcionou

No novo PowerShell, execute:

```powershell
echo $env:SMTP_EMAIL
echo $env:SMTP_PASSWORD
```

Se aparecerem os valores que você configurou, está tudo certo! ✅

---

## 💻 Método 2: Via PowerShell (Temporário - Apenas na Sessão Atual)

Execute no PowerShell **antes** de rodar `docker compose up`:

```powershell
# Configure as variáveis (válidas apenas nesta sessão do PowerShell)
$env:SMTP_EMAIL = "seu-email@gmail.com"
$env:SMTP_PASSWORD = "sua-senha-de-app"

# Verificar se foram configuradas
echo $env:SMTP_EMAIL
echo $env:SMTP_PASSWORD
```

**⚠️ Limitação:** Essas variáveis só existem enquanto o PowerShell estiver aberto. Ao fechar, elas são perdidas.

---

## 💻 Método 3: Via PowerShell (Permanente - Adiciona ao Sistema)

Execute no PowerShell **como Administrador**:

```powershell
# Adicionar variáveis permanentemente ao usuário atual
[System.Environment]::SetEnvironmentVariable("SMTP_EMAIL", "seu-email@gmail.com", "User")
[System.Environment]::SetEnvironmentVariable("SMTP_PASSWORD", "sua-senha-de-app", "User")

# Verificar
[System.Environment]::GetEnvironmentVariable("SMTP_EMAIL", "User")
[System.Environment]::GetEnvironmentVariable("SMTP_PASSWORD", "User")
```

**Após executar:** Feche e abra novamente o PowerShell para que as variáveis sejam carregadas.

---

## ✅ Como Verificar se Estão Configuradas

### Verificação Rápida no PowerShell

Abra um **novo PowerShell** (importante: feche e abra novamente após configurar as variáveis) e execute:

```powershell
# Verificar SMTP_EMAIL
echo $env:SMTP_EMAIL
```

**Resultado esperado:** Deve mostrar seu e-mail, por exemplo: `seu-email@gmail.com`

```powershell
# Verificar SMTP_PASSWORD
echo $env:SMTP_PASSWORD
```

**Resultado esperado:** Deve mostrar a senha de app de 16 caracteres (sem espaços)

### Verificação Completa

Para listar todas as variáveis relacionadas a SMTP:

```powershell
Get-ChildItem Env: | Where-Object { $_.Name -like "*SMTP*" }
```

**Resultado esperado:** Deve mostrar duas variáveis:
- `SMTP_EMAIL` com seu e-mail
- `SMTP_PASSWORD` com a senha de app

### ⚠️ Problemas Comuns

**Se aparecer vazio ou nada:**
- Você não fechou e abriu novamente o PowerShell após configurar
- As variáveis foram configuradas apenas para a sessão atual (método temporário)
- As variáveis foram configuradas em "Variáveis do sistema" mas você está verificando "Variáveis de usuário"

**Solução:**
1. Feche completamente o PowerShell
2. Abra um novo PowerShell
3. Execute os comandos de verificação novamente

**Se ainda não aparecer:**
1. Verifique se configurou corretamente seguindo o [Método 1](#-método-1-via-interface-gráfica-do-windows-recomendado---permanente)
2. Certifique-se de ter clicado em "OK" em todas as janelas
3. Reinicie o computador (último recurso)

---

## 📧 Como Gerar Senha de App do Gmail (Passo a Passo Completo)

Para usar o Gmail como servidor SMTP, você **NÃO pode usar a senha normal da sua conta**. É necessário gerar uma **Senha de App** específica para aplicativos.

### ⚠️ Pré-requisito: Verificação em Duas Etapas Ativada

A verificação em duas etapas **DEVE estar ativada** antes de gerar uma senha de app. Se ainda não ativou, siga os passos abaixo.

---

### Parte 1: Ativar Verificação em Duas Etapas (Se Ainda Não Estiver Ativada)

1. **Acesse sua conta Google:**
   - Abra o navegador e vá para: https://myaccount.google.com/security
   - Faça login se necessário

2. **Localize a seção "Como fazer login no Google":**
   - Role a página até encontrar essa seção

3. **Encontre "Verificação em duas etapas":**
   - Procure por "Verificação em duas etapas" ou "2-Step Verification"
   - Clique em **"Verificação em duas etapas"** ou **"Começar"**

4. **Siga o processo de ativação:**
   - O Google vai pedir para confirmar seu número de telefone
   - Você receberá um código por SMS ou chamada
   - Digite o código recebido
   - Siga as instruções na tela para concluir

5. **Confirme que está ativada:**
   - Volte para https://myaccount.google.com/security
   - Verifique se "Verificação em duas etapas" mostra como **"Ativada"**

---

### Parte 2: Gerar a Senha de App

Agora que a verificação em duas etapas está ativada, você pode gerar a senha de app:

#### Passo 1: Acessar a Página de Senhas de App

1. **Abra o navegador e acesse:**
   ```
   https://myaccount.google.com/apppasswords
   ```

2. **Faça login** se necessário

3. **Se aparecer uma mensagem pedindo para selecionar o app:**
   - Isso significa que você precisa primeiro escolher o tipo de app
   - Continue para o próximo passo

#### Passo 2: Selecionar o Tipo de App e Dispositivo

1. Na página de Senhas de App, você verá dois menus suspensos:

   **Menu 1 - "Selecione o app":**
   - Clique no menu suspenso
   - Selecione **"Mail"** (Correio)

   **Menu 2 - "Selecione o dispositivo":**
   - Clique no menu suspenso
   - Selecione **"Outro (nome personalizado)"** (última opção)
   - Uma caixa de texto aparecerá
   - Digite um nome descritivo, por exemplo: **"Prenatal Digital SUS"** ou **"Prenatal Alertas"**
   - Clique em **"Gerar"**

#### Passo 3: Copiar a Senha Gerada

1. **Uma senha de 16 caracteres será gerada:**
   - A senha aparecerá em uma caixa amarela
   - Formato: 16 caracteres separados em grupos de 4 (ex: `abcd efgh ijkl mnop`)
   - **IMPORTANTE:** Copie essa senha AGORA, pois ela só aparece uma vez!

2. **Como copiar:**
   - Clique no botão de copiar ao lado da senha (ícone de dois quadrados sobrepostos)
   - Ou selecione todo o texto da senha e pressione `Ctrl + C`
   - **DICA:** Cole em um bloco de notas temporário para não perder

3. **Remover espaços (se houver):**
   - A senha pode aparecer com espaços: `abcd efgh ijkl mnop`
   - Remova todos os espaços: `abcdefghijklmnop`
   - Use essa versão sem espaços na variável de ambiente

#### Passo 4: Usar a Senha de App

1. **A senha de app gerada é o que você vai usar em `SMTP_PASSWORD`:**
   - **NÃO** use sua senha normal do Gmail
   - **USE** a senha de 16 caracteres que acabou de gerar (sem espaços)

2. **Configure a variável de ambiente:**
   - Siga as instruções da seção "Método 1: Via Interface Gráfica do Windows" acima
   - Na variável `SMTP_PASSWORD`, cole a senha de app (16 caracteres, sem espaços)

3. **Exemplo:**
   - Se a senha gerada foi: `abcd efgh ijkl mnop`
   - Use na variável: `abcdefghijklmnop` (sem espaços)

---

### 📝 Resumo Rápido - Gmail

1. ✅ Ative verificação em duas etapas: https://myaccount.google.com/security
2. ✅ Gere senha de app: https://myaccount.google.com/apppasswords
   - App: **Mail**
   - Dispositivo: **Outro (nome personalizado)** → Digite "Prenatal Digital SUS"
3. ✅ Copie a senha de 16 caracteres (sem espaços)
4. ✅ Configure `SMTP_EMAIL` = seu e-mail Gmail completo
5. ✅ Configure `SMTP_PASSWORD` = senha de app gerada (16 caracteres, sem espaços)

---

### 🔍 Como Verificar se a Senha de App Está Funcionando

Após configurar as variáveis e subir o projeto:

1. **Suba os serviços:**
   ```powershell
   docker compose up --build -d
   ```

2. **Verifique os logs do prenatal-alertas:**
   ```powershell
   docker compose logs -f prenatal-alertas
   ```

3. **Se estiver funcionando:**
   - Você verá logs normais sem erros de autenticação SMTP
   - Quando o job de alertas executar, e-mails serão enviados

4. **Se houver erro:**
   - Verifique se copiou a senha corretamente (sem espaços)
   - Verifique se a verificação em duas etapas está ativada
   - Verifique se usou a senha de app, não a senha normal da conta

---

### 📧 Para Outros Provedores de E-mail (Outlook, Yahoo, etc.)

Se você não usar Gmail, consulte a documentação do seu provedor:

- **Outlook/Hotmail:** Geralmente usa `smtp.office365.com:587` e pode precisar de senha de app também
- **Yahoo:** Geralmente usa `smtp.mail.yahoo.com:587` e requer senha de app
- **Provedores corporativos:** Consulte o administrador de TI

**Nota:** O projeto está configurado para usar `smtp.gmail.com:587` por padrão. Se usar outro provedor, você pode precisar ajustar o arquivo `prenatal-alertas/src/main/resources/application.properties`.

---

## 🐳 Como o Docker Compose Obtém as Variáveis da Máquina

As variáveis **devem estar definidas nas variáveis de ambiente da máquina**. Ao executar `docker compose up`, o Docker Compose **obtém** os valores das variáveis de ambiente definidas na máquina e repassa aos containers.

O `docker-compose.yml` usa a sintaxe `${SMTP_EMAIL:-}`, que significa:
- **Se `SMTP_EMAIL` existir** nas variáveis de ambiente da máquina → usa esse valor e repassa ao container
- **Se não existir** → usa string vazia (`-`)

Não é usado arquivo `.env` no projeto; a fonte das variáveis é sempre o ambiente da máquina.

---

## 🚀 Testando a Configuração Completa

Após configurar as variáveis de ambiente, siga estes passos para testar:

### Passo 1: Verificar Variáveis no PowerShell

1. **Feche completamente o PowerShell** (se você acabou de configurar as variáveis)

2. **Abra um novo PowerShell**

3. **Navegue até a pasta do projeto:**
   ```powershell
   cd C:\projetos\Fiap\prenatal-digital-sus
   ```

4. **Verifique se as variáveis estão carregadas:**
   ```powershell
   echo $env:SMTP_EMAIL
   echo $env:SMTP_PASSWORD
   ```
   
   **✅ Se aparecerem os valores:** Continue para o próximo passo
   
   **❌ Se estiverem vazias:** Volte para a seção [Como Verificar](#-como-verificar-se-estão-configuradas) e resolva o problema

### Passo 2: Subir os Serviços

```powershell
docker compose up --build -d
```

Aguarde alguns minutos para todos os serviços iniciarem. Você pode acompanhar o progresso com:

```powershell
docker compose logs -f
```

Pressione `Ctrl + C` para sair dos logs quando quiser.

### Passo 3: Verificar Logs do Serviço de Alertas

O serviço `prenatal-alertas` é responsável por enviar e-mails. Verifique seus logs:

```powershell
docker compose logs -f prenatal-alertas
```

#### ✅ Sinais de Sucesso:

- **Se as variáveis estiverem configuradas corretamente:**
  - Você verá logs normais de inicialização
  - Não aparecerão mensagens de erro sobre SMTP não configurado
  - Quando o job de análise executar (a cada alguns minutos), você verá mensagens como:
    ```
    Análise de gestações: X paciente(s) ativo(s)
    E-mail enviado diretamente (SMTP) para: seu-email@gmail.com
    ```

#### ❌ Sinais de Problema:

- **Se aparecer esta mensagem:**
  ```
  Envio direto SMTP: SMTP_EMAIL não configurado. Defina a variável de ambiente e reinicie.
  ```
  **Solução:** As variáveis não estão sendo lidas. Verifique:
  1. Se configurou corretamente seguindo o [Método 1](#-método-1-via-interface-gráfica-do-windows-recomendado---permanente)
  2. Se fechou e abriu novamente o PowerShell
  3. Se reiniciou os containers após configurar: `docker compose restart prenatal-alertas`

- **Se aparecer erro de autenticação SMTP:**
  ```
  Authentication failed
  Invalid credentials
  ```
  **Solução:** 
  1. Verifique se está usando a **senha de app** do Gmail, não a senha normal
  2. Verifique se a senha de app foi copiada corretamente (sem espaços)
  3. Verifique se a verificação em duas etapas está ativada
  4. Gere uma nova senha de app se necessário

### Passo 4: Testar Envio Real de E-mail

Para testar se o e-mail está sendo enviado:

1. **Certifique-se de ter dados no sistema:**
   - Uma gestante cadastrada no prontuário com e-mail válido
   - Dados que gerem alertas (exames pendentes, vacinas em atraso, etc.)

2. **Aguarde o job executar:**
   - O job de análise roda periodicamente (verifique a configuração no código)
   - Ou force a execução se houver endpoint para isso

3. **Verifique a caixa de entrada:**
   - O e-mail será enviado para o endereço cadastrado no prontuário da gestante
   - Verifique também a pasta de spam/lixo eletrônico

---

## 🔧 Troubleshooting (Solução de Problemas)

### Problema 1: Variáveis não aparecem no PowerShell

**Sintomas:**
```powershell
echo $env:SMTP_EMAIL
# Retorna vazio ou nada
```

**Soluções:**

1. **Verifique se configurou corretamente:**
   - Abra novamente as Variáveis de Ambiente (`sysdm.cpl` → Avançado → Variáveis de Ambiente)
   - Confirme que `SMTP_EMAIL` e `SMTP_PASSWORD` estão listadas em "Variáveis de usuário"
   - Verifique se os valores estão corretos (sem espaços extras no início/fim)

2. **Feche e abra novamente o PowerShell:**
   - Variáveis de ambiente são carregadas quando o PowerShell inicia
   - Se você configurou enquanto o PowerShell estava aberto, precisa fechar e abrir novamente

3. **Reinicie o computador:**
   - Em alguns casos raros, pode ser necessário reiniciar

### Problema 2: Docker Compose não lê as variáveis

**Sintomas:**
- Logs mostram: `SMTP_EMAIL não configurado`
- E-mails não são enviados

**Soluções:**

1. **Verifique se as variáveis estão no ambiente do Windows:**
   ```powershell
   echo $env:SMTP_EMAIL
   echo $env:SMTP_PASSWORD
   ```
   Se não aparecerem, resolva primeiro o Problema 1

2. **Reinicie os containers:**
   ```powershell
   docker compose restart prenatal-alertas
   ```

3. **Verifique se está usando o PowerShell correto:**
   - Use PowerShell ou CMD do Windows
   - Não use Git Bash ou outros terminais que podem não ler variáveis do Windows

### Problema 3: Erro de autenticação SMTP

**Sintomas:**
- Logs mostram: `Authentication failed` ou `Invalid credentials`
- E-mails não são enviados

**Soluções:**

1. **Verifique se está usando senha de app:**
   - Gmail **NÃO aceita** a senha normal da conta
   - Você **DEVE** usar uma senha de app gerada em https://myaccount.google.com/apppasswords

2. **Verifique se a senha foi copiada corretamente:**
   - A senha de app tem 16 caracteres
   - Remova todos os espaços (se houver)
   - Não adicione espaços extras no início ou fim

3. **Verifique se a verificação em duas etapas está ativada:**
   - Senhas de app só funcionam se a verificação em duas etapas estiver ativada
   - Verifique em: https://myaccount.google.com/security

4. **Gere uma nova senha de app:**
   - Às vezes é necessário gerar uma nova senha de app
   - Acesse: https://myaccount.google.com/apppasswords
   - Gere uma nova e atualize a variável `SMTP_PASSWORD`

### Problema 4: E-mails não chegam

**Sintomas:**
- Logs mostram que o e-mail foi enviado
- Mas o e-mail não chega na caixa de entrada

**Soluções:**

1. **Verifique a pasta de spam/lixo eletrônico:**
   - E-mails podem ser filtrados como spam
   - Verifique também a pasta "Promoções" no Gmail

2. **Verifique o endereço de e-mail no prontuário:**
   - O e-mail é enviado para o endereço cadastrado no prontuário da gestante
   - Confirme que o e-mail está correto no banco de dados

3. **Verifique os logs do prenatal-alertas:**
   - Procure por mensagens de erro específicas
   - Verifique se há problemas de conexão com o servidor SMTP

### Problema 5: Não consigo gerar senha de app no Gmail

**Sintomas:**
- A página https://myaccount.google.com/apppasswords não mostra a opção de gerar senha

**Soluções:**

1. **Verifique se a verificação em duas etapas está ativada:**
   - Senhas de app só aparecem se a verificação em duas etapas estiver ativada
   - Ative em: https://myaccount.google.com/security

2. **Use uma conta Google pessoal:**
   - Contas corporativas/escolares podem ter restrições
   - Tente com uma conta Gmail pessoal

3. **Verifique se sua conta permite:**
   - Algumas contas podem ter restrições de segurança
   - Verifique as configurações de segurança da conta

---

## 📚 Referências

- [Documentação Docker Compose - Environment Variables](https://docs.docker.com/compose/environment-variables/)
- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Gmail - Senhas de App](https://support.google.com/accounts/answer/185833)
