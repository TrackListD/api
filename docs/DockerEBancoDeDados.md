# 🎵 TrackListD — Guia de Setup do Ambiente

Este guia explica como configurar e rodar a aplicação localmente. Siga os passos na ordem indicada.

---

## Pré-requisitos

Antes de começar, você precisa ter instalado:

- [Git](https://git-scm.com/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Windows/Mac) ou [Docker Engine](https://docs.docker.com/engine/install/) (Linux)
- [Java 21](https://adoptium.net/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recomendado) ou outra IDE Java

---

## 1. Criar o arquivo `.env`

Na raiz do projeto existe um arquivo `.env.example`. Você precisa copiá-lo e preencher com suas credenciais locais.

```bash
# Linux/Mac
cp .env-example .env

# Windows (PowerShell)
Copy-Item .env-example .env
```

Abra o `.env` e preencha os valores:

```env
DB_HOST=localhost
DB_PORT=3307
DB_NAME=TrackList_Schema
DB_USERNAME=TrackList
DB_PASSWORD=sua_senha_aqui
DB_ROOT_PASSWORD=sua_senha_root_aqui
```

> ⚠️ O `.env` nunca deve ser commitado. Ele já está no `.gitignore`.

---

## 2. Subir o banco de dados com Docker

Com o Docker rodando, execute na raiz do projeto:

```bash
docker compose up -d
```

Aguarde alguns segundos e verifique se o banco está pronto:

```bash
docker compose logs db
```

Procure pela linha:
```
ready for connections. Version: '8.4.x' ... port: 3306
```

Quando aparecer, o banco está pronto para receber conexões.

---

## 3. Rodar a aplicação

**Pelo IntelliJ:**

1. Abra o projeto no IntelliJ
2. Vá em `Run > Edit Configurations`
3. Em `Active profiles`, coloque: `dev`
4. Clique em **Run** na classe `ApiApplication`

**Pelo terminal:**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## 4. Verificar se funcionou

No console da aplicação, procure pelas linhas:

```
The following 1 profile is active: "dev"
Started ApiApplication in X.XXX seconds
Tomcat started on port 8080
```

Se aparecer, a API está rodando em `http://localhost:8080`.

---

## Ordem de inicialização

Sempre siga essa ordem:

```
1. docker compose up -d     → sobe o banco
2. aguarda "ready for connections" nos logs
3. roda a aplicação Spring
```

Se o Spring subir antes do banco estar pronto, a conexão vai falhar.

---

## Problemas comuns

**`Access denied for user`**

As credenciais do `.env` não batem com as que o Docker usou para criar o banco. Solução:

```bash
docker compose down
docker volume rm api_mysql_home
docker compose up -d
```

Aguarda o banco inicializar e tenta novamente.

**Porta 3307 ocupada**

Algum outro serviço está usando a porta 3307. Troque a porta no `compose.yaml` e no `.env` para um valor livre, como `3308`.

---

## Parar o ambiente

```bash
# Para a aplicação: Ctrl+C no terminal ou Stop no IntelliJ

# Para o banco:
docker compose down
```

> O volume de dados persiste mesmo após `docker compose down`. Seus dados locais não são perdidos — a menos que você rode `docker volume rm`.
