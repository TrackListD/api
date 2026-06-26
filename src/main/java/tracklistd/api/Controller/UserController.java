package tracklistd.api.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;

import tracklistd.api.Mapper.UserMapper;
import tracklistd.api.Dto.User.UserMinResponseDTO;
import tracklistd.api.Dto.User.UserRegisterResponseDTO;
import tracklistd.api.Dto.User.UserPerfilResponseDTO;
import tracklistd.api.Dto.User.UserRegisterRequestDTO;
import tracklistd.api.Dto.User.UserUpdatePerfilRequestDTO;
import tracklistd.api.Service.UserService;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.UserExceptions.FollowYourself;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

        private final UserService userService;
        private final UserMapper userMapper;

        public UserController(UserService userService, UserMapper userMapper) {
                this.userService = userService;
                this.userMapper = userMapper;
        }

        @Operation(summary = "Registrar um novo usuário", description = "Cria um novo perfil associando os dados recebidos a um identificador de login externo.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRegisterResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Dados da requisição ausentes ou inválidos", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Conflito: este ID de login já está em uso por outra conta", content = @Content)
        })
        @PostMapping
        public ResponseEntity<UserRegisterResponseDTO> register(@Valid @RequestBody UserRegisterRequestDTO dto) {
                User registeredUser = userService.register(dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toRegisterDto(registeredUser));
        }

        @Operation(summary = "Buscar usuário por ID", description = "Retorna as informações completas de um perfil utilizando o seu identificador numérico único.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuário encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserPerfilResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Recurso não encontrado: não existe perfil com este ID", content = @Content)
        })
        @GetMapping("/{id}")
        public ResponseEntity<UserPerfilResponseDTO> findUserById(@PathVariable Long id) {
                User user = userService.findUserById(id);
                return ResponseEntity.ok(userMapper.toPerfilDto(user));
        }

        @Operation(summary = "Buscar meu próprio perfil", description = "Retorna as informações completas do perfil logado.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Meu perfil carregado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserPerfilResponseDTO.class)))
        })
        @GetMapping("/me")
        public ResponseEntity<UserPerfilResponseDTO> findMyProfile(@AuthenticationPrincipal User user) {
                User fullUser = userService.findUserById(user.getId());
                return ResponseEntity.ok(userMapper.toPerfilDto(fullUser));
        }

        @Operation(summary = "Atualizar perfil", description = "Edita os campos alteráveis de exibição ou configurações do perfil informado.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Perfil atualizado com êxito", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserPerfilResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Problemas na formatação dos dados enviados", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Falha na busca: este perfil não existe", content = @Content)
        })
        @PutMapping("/me")
        public ResponseEntity<UserPerfilResponseDTO> perfilUpdate(@AuthenticationPrincipal User user,
                        @Valid @RequestBody UserUpdatePerfilRequestDTO dto) {
                User updatedUser = userService.perfilUpdate(user.getId(), dto);
                return ResponseEntity.ok(userMapper.toPerfilDto(updatedUser));
        }

        @Operation(summary = "Deletar conta", description = "Remove permanentemente o perfil do usuário autenticado e seus dados associados.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Conta removida com sucesso", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Falha na busca: este perfil não existe", content = @Content)
        })
        @DeleteMapping("/me")
        public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal User user) {
                userService.deleteAccount(user.getId());
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Seguir perfil", description = "Estabelece a conexão social indicando que a sua conta passou a seguir um alvo específico.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Ação de relacionamento processada e vinculada com sucesso", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Tentativa ilógica flagrada: requisição bloqueada por regra interna", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Falha de consistência: um dos identificadores não tem registro correspondente ativo", content = @Content)
        })
        @PostMapping("/follow/{friendId}")
        public ResponseEntity<Void> followUser(@AuthenticationPrincipal User user, @PathVariable Long friendId) {
                userService.followUser(user.getId(), friendId);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Deixar de seguir perfil", description = "Extingue a conexão social criada anteriormente, removendo a conta alvo da sua lista de seguidores.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Ação processada e vínculo quebrado com sucesso", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Tentativa ilógica flagrada: requisição bloqueada por regra interna", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Falha de consistência: um dos identificadores não tem registro correspondente ativo", content = @Content)
        })
        @DeleteMapping("/follow/{friendId}")
        public ResponseEntity<Void> unfollowUser(@AuthenticationPrincipal User user, @PathVariable Long friendId) {
                userService.unfollowUser(user.getId(), friendId);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Listar Seguidores", description = "Retorna uma lista resumida das pessoas que seguem o usuário especificado.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Seguidores listados com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserMinResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Falha na busca: este perfil não existe", content = @Content)
        })
        @GetMapping("/{id}/followers")
        public ResponseEntity<List<UserMinResponseDTO>> getFollowers(@PathVariable Long id) {
                List<UserMinResponseDTO> followers = userService.getFollowers(id).stream()
                                .map(userMapper::toMinDto)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(followers);
        }

        @Operation(summary = "Listar Seguindo", description = "Retorna uma lista resumida das pessoas que o usuário especificado está seguindo.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Amigos listados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserMinResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Falha na busca: este perfil não existe", content = @Content)
        })
        @GetMapping("/{id}/following")
        public ResponseEntity<List<UserMinResponseDTO>> getFollowing(@PathVariable Long id) {
                List<UserMinResponseDTO> following = userService.getFollowing(id).stream()
                                .map(userMapper::toMinDto)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(following);
        }
}