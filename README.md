# Guia de Workflow

Este documento define o padrão de desenvolvimento da Organização no GitHub, garantindo organização, qualidade e automação do fluxo de trabalho.

---

# 📌 Sumário

1. [Gestão de Tarefas (GitHub Projects & Issues)](#1-gestão-de-tarefas-github-projects--issues)
2. [Fluxo de Trabalho com GitFlow](#2-fluxo-de-trabalho-com-gitflow)
3. [Conventional Commits & Automação](#3-conventional-commits--automação)
4. [A Regra de Ouro: Qualidade na Origem](#4-a-regra-de-ouro-qualidade-na-origem)
5. [Processo de Pull Request (PR) & Code Review Livre](#5-processo-de-pull-request-pr--code-review-livre)
6. [Padrões de Documentação (Swagger & Doxygen)](#6-padrões-de-documentação-swagger--doxygen)

---

# 1. Gestão de Tarefas (GitHub Projects & Issues)

Para manter todo o nosso fluxo centralizado onde o código reside, **substituímos completamente o Trello pelo GitHub Projects**.

## O Quadro Kanban (Projects)

Na aba **Projects** da nossa Organização, temos o nosso quadro global. Ele é dividido em colunas assim como o trello.

- **Backlog (A Fazer):** Tarefas que ainda não foram iniciadas.
- **In Progress (Fazendo):** Tarefas que estão sendo trabalhadas.
- **Review (Em Revisão):** Tarefa aguardando Reviw=ew
- **Done (Concluído):** Tarefa Completada

## As Issues

Qualquer funcionalidade nasce como uma **Issue** dentro do repositório correspondente (`frontend` ou `backend`).

1. **Criação:** Crie a Issue descrevendo de forma clara os requisitos e o escopo da tarefa.
2. **Vínculo:** No menu lateral direito da Issue, associe-a ao quadro da Organização na seção **Projects**. Ela aparecerá automaticamente no Kanban.
3. **Branches:** Na pagina de criação da Issue, é possivel criar a branch que você vai trabalhar.

---

# 2. GitFlow

Branches Principais

- `main`: contém apenas o código em estado de produção.
- `develop`: é a nossa branch de integração. Todo desenvolvimento do dia a dia vem para cá.

## Branches de Trabalho

Sempre que você for iniciar uma Issue, você deve criar uma ramificação a partir da `develop` seguindo o padrão de nomes:

- `feature/nome-da-tarefa`
  - Exemplo: `feature/sistema-resenhas`
- `bugfix/nome-do-erro`
  - Exemplo: `bugfix/correcao-login`

## Passo a Passo no Terminal

```bash
# 1. Garanta que sua develop local está atualizada
git checkout develop
git pull origin develop

# 2. Crie e mude para a sua nova branch de feature
git checkout -b feature/minha-nova-funcionalidade

# 3. Desenvolva seu código localmente...
```

---

# 3. Conventional Commits & Automação

Adotamos o padrão de **Conventional Commits**.

## Estrutura de Commit

```text
<tipo>(<escopo>): <descrição curta em letras minúsculas>
```

## Tipos Comuns (`<tipo>`)

- `feat`: uma nova funcionalidade para o sistema.
- `fix`: solução de um problema/bug.
- `docs`: alterações apenas na documentação.
- `style`: mudanças de formatação que não afetam a lógica.
- `refactor`: melhoria de arquitetura sem correção de bug nem nova funcionalidade.
- `test`: adição ou modificação de testes.

## Exemplos de Commits

```bash
git commit -m "feat(reviews): adicionar sistema de notas por estrelas para filmes"

git commit -m "fix(database): corrigir vazamento de conexao no repositorio de usuarios"
```

## Automação de Fechamento de Issues

Podemos fazer com que o GitHub feche uma Issue e mova o cartão no Kanban para **Done** automaticamente.

Para isso, use palavras-chave como:

- `Closes #numero da Issue`
- `Fixes #numero da Issue`
- `Resolves #numero da Issue`

no corpo do commit ou na descrição do Pull Request.

### Exemplo

```bash
git commit -m "feat(reports): criar endpoint para denuncia de comentarios ofensivos

Closes #42"
```

---

# 4. A Regra de Ouro: Qualidade na Origem

> ⚠️ REGRA INEGOCIÁVEL:
> Tudo o que foi commitado e enviado para o repositório remoto deve estar funcionando perfeitamente antes de ser levado para um Pull Request.

A responsabilidade pela qualidade do funcionamento é de quem desenvolve, não de quem revisa.

## Padrão de Linguagem

Nomes de funções, variáveis, classes e arquivos devem estar em inglês
Comentários internos, explicações, documentação e Codigo podem estar em português
Evite nomes genéricos ou abreviações sem contexto

---

# 5. Processo de Pull Request (PR) & Code Review Livre

Quando sua funcionalidade estiver pronta e testada, é hora de abrir um Pull Request.

## Fluxo Esperado

1. Envie sua branch:

```bash
git push origin feature/sua-feature
```

2. Abra o Pull Request no GitHub:
   - origem: `feature/*`
   - destino: `develop`

3. Na descrição do PR:
   - explique a funcionalidade
   - link a issue correspondente
   - use `Closes #XX`

4. Mova o cartão da tarefa para a coluna **Review**.

---

## Code Review

Qualquer membro disponível pode revisar o código dos colegas.

### Aviso Coletivo

Assim que abrir o PR, envie o link ou uma mensagem no grupo:

```text
"Pessoal, PR da feature X aberto. Quem consegue dar uma olhada?"
```

### O que Olhar

Como o autor já garantiu que o código funciona, a revisão deve focar em algumas perguntas importantes:

- A lógica de negócio está na camada correta?
- O código está limpo e sem redundância?
- Existem cenários excepcionais não tratados?

> É necessário no mínimo 1 aprovação de outro membro para liberar o merge.

---

# 6. Padrões de Documentação (Swagger & Doxygen)

Um código profissional precisa ser bem documentado para facilitar o entendimento da equipe e dos avaliadores do projeto.

---

## Swagger

Utilizado para documentação da API.

Documente:

- endpoints
- parâmetros
- respostas
- códigos de erro

## Doxygen

Utilizado para documentar:

- classes
- métodos
- arquitetura
- regras de negócio

## Boas Práticas

- Evite documentar o óbvio
- Priorize clareza no código
- Documente comportamentos complexos e decisões importantes

---

# Tracklistd — Guia de Setup do Ambiente de Desenvolvimento

O Tracklistd é composto por dois repositórios separados:

- **`api`**: back-end em Spring Boot (Java), executado via wrapper Maven (`./mvnw`).
- **`front`**: front-end em React Native com Expo, executado via `npx expo start`.

Antes de rodar qualquer um dos dois, é necessário baixar as dependências do projeto e configurar as variáveis de ambiente (`.env`) correspondentes.

## Pré-requisitos

- Node.js (LTS) e npm instalados, para o **front**.
- JDK compatível com o projeto instalado, para a **api** (o Maven em si não precisa estar instalado globalmente, pois o projeto usa o wrapper `mvnw`).
- Uma instância de banco de dados (MySQL/MariaDB) acessível localmente, geralmente via Docker, para a **api**.
- Conta de acesso ao Firebase Console e ao Spotify for Developers, para gerar as credenciais (ver seção [Variáveis de ambiente](#variáveis-de-ambiente-env)).

## Repositório `api` (Spring Boot)

### Instalar dependências

O wrapper do Maven baixa as dependências declaradas no `pom.xml` automaticamente na primeira execução. Para forçar o download sem rodar a aplicação:

```bash
./mvnw dependency:resolve
```

### Rodar a aplicação

Com o `.env` configurado (ver [`.env` da api](#env-da-api)) e o banco de dados acessível:

```bash
./mvnw spring-boot:run
```

Esse comando sobe a api usando apenas o `application.properties` padrão. Para escolher um profile específico, veja [Profiles do Spring Boot](#profiles-do-spring-boot).

### Profiles do Spring Boot

A `api` possui múltiplos arquivos de configuração em `src/main/resources/`, um para cada profile:

| Arquivo                           | Profile    | Uso                                    |
| --------------------------------- | ---------- | -------------------------------------- |
| `application.properties`          | _(base)_   | Configuração base, sempre carregada    |
| `application-dev.properties`      | `dev`      | Desenvolvimento local                  |
| `application-populate.properties` | `populate` | Popula o banco com dados iniciais/seed |
| `application-prod.properties`     | `prod`     | Produção                               |

Quando um profile é ativado, o Spring Boot carrega o `application.properties` normalmente e, em seguida, sobrepõe/complementa com as chaves do arquivo específico do profile (`application-{profile}.properties`).

Para ativar um profile ao rodar via Maven wrapper, use a flag `-Dspring-boot.run.profiles`:

```bash
# Profile de desenvolvimento
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Profile de populacao do banco
./mvnw spring-boot:run -Dspring-boot.run.profiles=populate

# Profile de producao
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

Alternativamente, o profile também pode ser definido por variável de ambiente antes de subir a aplicação, o que é útil em pipelines de CI/CD ou containers:

```bash
export SPRING_PROFILES_ACTIVE=prod
./mvnw spring-boot:run
```

> Rodar sem nenhuma flag de profile (`./mvnw spring-boot:run`) usa apenas o `application.properties`, sem nenhum dos overrides de `dev`, `populate` ou `prod`.

## Repositório `front` (React Native / Expo)

### Instalar dependências

```bash
npm install
```

### Rodar a aplicação

Com o `.env` configurado (ver [`.env` do front](#env-do-front)):

```bash
npx expo start
```

## Variáveis de ambiente (`.env`)

Cada repositório possui seu próprio arquivo `.env` na raiz, que **não deve ser versionado** (deve constar no `.gitignore`). Abaixo estão as chaves esperadas em cada um e como obter os respectivos valores. Os valores em si não são reproduzidos aqui por segurança.

### `.env` do `front`

**Chaves do Firebase** — `EXPO_PUBLIC_FIREBASE_API_KEY`, `EXPO_PUBLIC_FIREBASE_AUTH_DOMAIN`, `EXPO_PUBLIC_FIREBASE_PROJECT_ID`, `EXPO_PUBLIC_FIREBASE_STORAGE_BUCKET`, `EXPO_PUBLIC_FIREBASE_MESSAGING_SENDER_ID`, `EXPO_PUBLIC_FIREBASE_APP_ID`, `EXPO_PUBLIC_FIREBASE_MEASUREMENT_ID`:

1. Acesse o [Firebase Console](https://console.firebase.google.com/) e selecione o projeto `tracklistd`.
2. Vá em **Configurações do projeto** (ícone de engrenagem) → **Geral**.
3. Na seção **Seus apps**, selecione o app Web registrado (ou crie um, se ainda não existir).
4. O bloco de configuração do SDK (`firebaseConfig`) exibirá todos esses valores prontos para copiar.

**Client IDs do Google Sign-In** — `EXPO_PUBLIC_WEB_CLIENT_ID`, `EXPO_PUBLIC_IOS_CLIENT_ID`:

1. Acesse o [Google Cloud Console](https://console.cloud.google.com/), no mesmo projeto vinculado ao Firebase.
2. Vá em **APIs & Serviços** → **Credenciais**.
3. Em **IDs de cliente OAuth 2.0**, localize o cliente do tipo _Web application_ (para `WEB_CLIENT_ID`) e o cliente do tipo _iOS_ (para `IOS_CLIENT_ID`).
4. Caso não existam, crie-os pelo próprio Firebase Authentication (Método de login → Google), que gera esses clientes automaticamente.

### `.env` da `api`

**Credenciais de banco de dados** — `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_ROOT_PASSWORD`, `DB_USERNAME`, `DB_PASSWORD`:

Não são obtidas de nenhum serviço externo — são definidas por quem configura o ambiente local, normalmente através de um `docker-compose.yml` que sobe uma instância MySQL/MariaDB. Os valores devem ser os mesmos usados na configuração do container do banco (host, porta exposta, nome do schema e credenciais do usuário).

**Credenciais do Spotify** — `SPOTIFY_CLIENT_ID`, `SPOTIFY_CLIENT_SECRET`:

1. Acesse o [Spotify for Developers Dashboard](https://developer.spotify.com/dashboard).
2. Selecione o app registrado do Tracklistd (ou crie um novo app, caso necessário).
3. Em **Settings**, o _Client ID_ fica visível diretamente; o _Client Secret_ pode ser revelado clicando em **View client secret**.

**Credencial de administrador do Firebase** — `FIREBASE_JSON_PATH`:

1. No [Firebase Console](https://console.firebase.google.com/), vá em **Configurações do projeto** → **Contas de serviço**.
2. Clique em **Gerar nova chave privada**, o que baixa um arquivo JSON com as credenciais do Admin SDK.
3. Coloque esse arquivo em `src/main/resources/firebase/` dentro do repositório `api`.
4. Aponte `FIREBASE_JSON_PATH` no `.env` para o caminho relativo desse arquivo.

## Observações de segurança

- Nunca faça commit dos arquivos `.env` nem do JSON do Firebase Admin SDK — ambos devem estar listados no `.gitignore`.
- O _Client Secret_ do Spotify e as senhas de banco são credenciais sensíveis: evite compartilhá-las em chats, tickets ou repositórios públicos.
- Caso alguma credencial sensível seja exposta acidentalmente, revogue/regenere-a o quanto antes (no Spotify Dashboard, no Firebase Console ou trocando a senha do banco).
