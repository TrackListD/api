# Guia de Documentação — TrackListD API

Este guia define o padrão de documentação adotado pelo time para a API do TrackListD.
Todo novo endpoint e DTO deve seguir esse padrão antes de abrir um Pull Request.

---

## Sumário

1. [O que é o Swagger e por que usamos](#1-o-que-é-o-swagger-e-por-que-usamos)
2. [Como acessar a UI localmente](#2-como-acessar-a-ui-localmente)
3. [Documentando DTOs com @Schema](#3-documentando-dtos-com-schema)
4. [Documentando Controllers com @Tag, @Operation e @ApiResponse](#4-documentando-controllers-com-tag-operation-e-apiresponse)
5. [Documentando parâmetros de rota e query com @Parameter](#5-documentando-parâmetros-de-rota-e-query-com-parameter)
6. [O que não documentar](#6-o-que-não-documentar)
7. [Checklist antes do PR](#7-checklist-antes-do-pr)

---

## 1. O que é o Swagger e por que usamos

O Swagger (baseado no padrão OpenAPI 3.1) gera automaticamente uma interface visual
interativa a partir das anotações no código. Ele serve como contrato entre o backend
e o frontend — o time de frontend consulta o Swagger para saber quais endpoints existem,
o que enviar e o que esperar receber, sem precisar ler o código Java.

A dependência utilizada é o **springdoc-openapi**, que integra o Swagger ao Spring Boot
automaticamente. A classe `SwaggerConfig` define as informações gerais da API.

---

## 2. Como acessar a UI localmente

1. Suba o projeto no IntelliJ clicando em **Run** (▶)
2. Aguarde a mensagem `Started TracklistDApplication` no console
3. Acesse no navegador:

```
http://localhost:8080/swagger-ui/documentation.html
```

> ⚠️ O Spring Security bloqueia essa rota em produção.
> Para desenvolvimento local, as rotas `/swagger-ui/**` e `/v3/api-docs/**`
> devem estar liberadas na `SecurityConfig`.

---

## 3. Documentando DTOs com @Schema

Use `@Schema` para descrever campos de DTOs na UI do Swagger.
Essa anotação não afeta o comportamento do código — é puramente documentação de contrato.

### No nível do record (descrição geral do DTO)

```java
@Schema(description = "DTO que recebe os dados necessários para criar uma Avaliação")
public record RatingRequestDto(...) {}
```

### No nível do campo

```java
@NotNull(message = "A Avaliação deve ter um alvo")
@Schema(
    description = "ID gerado pelo Spotify da mídia que será avaliada (Album ou Música)",
    example = "6rqhFgbbKwnb9MLmUQDhG6",
    requiredMode = Schema.RequiredMode.REQUIRED
)
String idTarget,
```

### Em campos de enum — sempre use `allowableValues`

```java
@Schema(
    description = "Define quem pode visualizar a Avaliação",
    example = "PUBLIC",
    requiredMode = Schema.RequiredMode.REQUIRED,
    allowableValues = {"PUBLIC", "JUST_FOLLOWERS", "PRIVATE"}
)
Privacy whoCanSee
```

### Em campos opcionais

```java
@Schema(
    description = "Texto dissertativo da Avaliação",
    example = "Produção incrível, melhor álbum do ano!"
)
String review,
```

### Regras do time

| Situação | O que fazer |
|---|---|
| Campo com nome autoexplicativo (`authorId`, `authorName`) | Não documentar |
| Campo abstrato ou com contexto de domínio (`targetId`, `review`) | Documentar com `description` e `example` |
| Campo enum | Sempre usar `allowableValues` |
| Campo obrigatório | Sempre usar `requiredMode = Schema.RequiredMode.REQUIRED` |
| Validações de valor (`@Min`, `@Max`) | Não duplicar no `@Schema` — deixar para o Bean Validation |

> ⚠️ **DTOs não aparecem na UI isoladamente.** O Swagger só exibe um DTO
> quando ele está associado a um endpoint — via `@RequestBody` na Controller
> ou via `content = @Content(schema = @Schema(implementation = ...))` em um
> `@ApiResponse`. Para verificar se o DTO foi registrado corretamente antes
> de ter uma Controller, acesse o contrato bruto em
> `http://localhost:8080/v3/api-docs` e procure pela seção `schemas`.

---

## 4. Documentando Controllers com @Tag, @Operation e @ApiResponse

### @Tag — agrupa endpoints na UI por domínio

Coloque no nível da classe:

```java
@Tag(name = "Avaliações", description = "Operações relacionadas a avaliações de músicas e álbuns")
@RestController
@RequestMapping("/ratings")
public class RatingController {}
```

### @Operation — descreve o que o endpoint faz

```java
@Operation(
    summary = "Busca uma avaliação pelo ID",
    description = "Retorna os dados públicos de uma avaliação. " +
                  "Respeita as configurações de privacidade do autor."
)
@GetMapping("/{id}")
public ResponseEntity<RatingResponseDto> findById(@PathVariable String id) {}
```

- `summary` → frase curta exibida na listagem de endpoints
- `description` → explicação completa, incluindo regras de negócio relevantes

### @ApiResponse — descreve cada possível resposta HTTP

```java
@ApiResponse(
    responseCode = "200",
    description = "Avaliação encontrada com sucesso",
    content = @Content(schema = @Schema(implementation = RatingResponseDto.class))
)
@ApiResponse(
    responseCode = "404",
    description = "Avaliação não encontrada para o ID informado",
    content = @Content
)
@ApiResponse(
    responseCode = "403",
    description = "Sem permissão para visualizar esta avaliação",
    content = @Content
)
```

- `content = @Content(schema = @Schema(implementation = MinhaClasse.class))` → indica o DTO retornado em respostas de sucesso
- `content = @Content` vazio → usado em respostas de erro, que não retornam corpo

### Exemplo completo de endpoint documentado

```java
@Tag(name = "Avaliações", description = "Operações relacionadas a avaliações de músicas e álbuns")
@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Operation(
        summary = "Cria uma nova avaliação",
        description = "Cria uma avaliação para um Album ou Música. " +
                      "Um usuário não pode avaliar a mesma mídia duas vezes."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Avaliação criada com sucesso",
        content = @Content(schema = @Schema(implementation = RatingResponseDto.class))
    )
    @ApiResponse(
        responseCode = "409",
        description = "Usuário já avaliou esta mídia",
        content = @Content
    )
    @ApiResponse(
        responseCode = "404",
        description = "Mídia não encontrada para o ID informado",
        content = @Content
    )
    @PostMapping
    public ResponseEntity<RatingResponseDto> create(@RequestBody @Valid RatingRequestDto dto) {
        // implementação
    }
}
```

---

## 5. Documentando parâmetros de rota e query com @Parameter

Use `@Parameter` para descrever `@PathVariable` e `@RequestParam`:

```java
@GetMapping("/{id}")
public ResponseEntity<RatingResponseDto> findById(
    @Parameter(description = "ID da avaliação", example = "42", required = true)
    @PathVariable Long id
) {}
```

```java
@GetMapping
public ResponseEntity<List<RatingResponseDto>> findAll(
    @Parameter(description = "Filtra avaliações por nota mínima", example = "3.5")
    @RequestParam(required = false) Float minNote
) {}
```

---

## 6. O que não documentar

> "Evite documentar o óbvio" — Diretrizes do TrackListD

Não use `@Schema` em campos cujo nome já é autoexplicativo no contexto da API:

```java
// Não precisa de @Schema — o nome já diz tudo
Long authorId,
String authorName,
LocalDateTime publicationDate,
Integer likeCount,
Integer commentCount
```

Não duplique validações do Bean Validation no `@Schema`:

```java
// Errado — duplicação desnecessária
@Min(value = 0)
@Max(value = 5)
@Schema(minimum = "0", maximum = "5") // redundante
Float ratingNote,

// Correto — cada anotação com sua responsabilidade
@Min(value = 0, message = "A nota deve ser maior ou igual a 0")
@Max(value = 5, message = "A nota deve ser menor ou igual a 5")
@Schema(description = "Nota da avaliação", example = "4.5", requiredMode = Schema.RequiredMode.REQUIRED)
Float ratingNote,
```

---

## 7. Checklist antes do PR

Antes de abrir um Pull Request com novos endpoints ou DTOs, verifique:

- [ ] Todos os DTOs de request têm `@Schema` nos campos não óbvios
- [ ] Todos os DTOs de response têm `@Schema` nos campos não óbvios
- [ ] Campos enum têm `allowableValues` definido
- [ ] A Controller tem `@Tag` no nível da classe
- [ ] Cada endpoint tem `@Operation` com `summary` e `description`
- [ ] Cada endpoint documenta todos os `@ApiResponse` possíveis (sucesso e erros)
- [ ] Respostas de sucesso têm `content` com o DTO correto
- [ ] Respostas de erro têm `content = @Content` vazio
- [ ] A UI do Swagger foi acessada localmente e o endpoint aparece corretamente

---

*Dúvidas? falar com o Almeida.*
