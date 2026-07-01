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
