package tracklistd.api.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import tracklistd.api.Dto.Feed.PublicationFeedDTO;
import tracklistd.api.Entity.User;
import tracklistd.api.Service.FeedService;

@RestController
@RequestMapping("/api/feed")
@Tag(name = "Feed", description = "Endpoints de geração e consumo de feeds")
public class FeedController {
    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/me")
    @Operation(summary = "Obter feed social do usuário", description = "Retorna as publicações personalizadas com base nos seguidores e interesses do usuário logado. Exige Token Bearer.")
    public ResponseEntity<List<PublicationFeedDTO>> socialFeed(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(feedService.getSocialFeed(user.getId()));
    }

    @GetMapping("/global")
    @Operation(summary = "Obter feed global da plataforma", description = "Retorna o feed público geral. Se o Token Bearer for enviado, processa dados personalizados (ex: se o usuário logado curtiu a publicação). Se omitido, funciona de forma anônima.")
    public ResponseEntity<List<PublicationFeedDTO>> globalFeed(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        Long userId = (user != null) ? user.getId() : null;
        return ResponseEntity.ok(feedService.getGlobalFeed(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<PublicationFeedDTO>> userFeed(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(feedService.getUserFeed(id, user.getId()));
    }
}