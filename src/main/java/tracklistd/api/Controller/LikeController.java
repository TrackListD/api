package tracklistd.api.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import tracklistd.api.Dto.Like.LikeResponseDTO;
import tracklistd.api.Dto.User.UserMinResponseDTO;
import tracklistd.api.Entity.User;
import tracklistd.api.Service.LikeService;

@RestController
@RequestMapping("/api/publications")
@Tag(name = "Curtidas", description = "Endpoints para gerenciar interações de curtidas em publicações")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "Alternar curtida (Like/Unlike)", description = "Adiciona ou remove a curtida do usuário autenticado na publicação informada no path. Exige Token Bearer.")
    public ResponseEntity<LikeResponseDTO> toggleLike(
            @Parameter(description = "ID da publicação a ser curtida/descurtida", example = "1") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        LikeResponseDTO response = likeService.toggleLike(user, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/likes")
    @Operation(summary = "Listar usuários que curtiram", description = "Retorna uma lista simplificada de todos os usuários que deram um like na publicação correspondente. Rota pública.")
    public ResponseEntity<List<UserMinResponseDTO>> seeWhoLiked(
            @Parameter(description = "ID da publicação desejada", example = "1") @PathVariable Long id) {
        List<UserMinResponseDTO> usersWhoLiked = likeService.getWhoLiked(id);
        return ResponseEntity.ok(usersWhoLiked);
    }
}