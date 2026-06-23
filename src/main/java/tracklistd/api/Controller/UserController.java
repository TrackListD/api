package tracklistd.api.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tracklistd.api.Service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import tracklistd.api.Dto.User.UserRegisterRequestDTO;
import tracklistd.api.Dto.User.UserUpdatePerfilRequestDTO;
import tracklistd.api.Entity.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Registrar um novo usuário", description = "Cria um novo perfil associando os dados recebidos a um identificador de login externo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Dados da requisição ausentes ou inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito: este ID de login já está em uso por outra conta",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<User> register(@RequestBody UserRegisterRequestDTO dto) {
        User registeredUser = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna as informações completas de um perfil utilizando o seu identificador numérico único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado: não existe perfil com este ID",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Atualizar perfil", description = "Edita os campos alteráveis de exibição ou configurações do perfil informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com êxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Problemas na formatação dos dados enviados",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Falha na busca: este perfil não existe",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> perfilUpdate(@PathVariable Long id, @RequestBody UserUpdatePerfilRequestDTO dto) {
        User updatedUser = userService.perfilUpdate(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Seguir perfil", description = "Estabelece a conexão social indicando que a sua conta passou a seguir um alvo específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ação de relacionamento processada e vinculada com sucesso", content = @Content),
            @ApiResponse(responseCode = "400", description = "Tentativa ilógica flagrada: requisição bloqueada por regra interna", content = @Content),
            @ApiResponse(responseCode = "404", description = "Falha de consistência: um dos identificadores não tem registro correspondente ativo", content = @Content)
    })
    @PostMapping("/{myId}/follow/{friendId}")
    public ResponseEntity<Void> followUser(@PathVariable Long myId, @PathVariable Long friendId) {
        userService.followUser(myId, friendId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deixar de seguir perfil", description = "Extingue a conexão social criada anteriormente, removendo a conta alvo da sua lista de seguidores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ação processada e vínculo quebrado com sucesso", content = @Content),
            @ApiResponse(responseCode = "400", description = "Tentativa ilógica flagrada: requisição bloqueada por regra interna", content = @Content),
            @ApiResponse(responseCode = "404", description = "Falha de consistência: um dos identificadores não tem registro correspondente ativo", content = @Content)
    })
    @DeleteMapping("/{myId}/follow/{friendId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long myId, @PathVariable Long friendId) {
        userService.unfollowUser(myId, friendId);
        return ResponseEntity.noContent().build();
    }
}