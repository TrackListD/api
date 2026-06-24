package tracklistd.api.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tracklistd.api.Dto.SpotifyAPI.SpotifySearchResponseDTO;
import tracklistd.api.Service.SpotifySearchService;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Spotify Search", description = "Endpoints para busca de mídias e artistas no Spotify")
public class SpotifySearchController {

    private final SpotifySearchService searchService;

    @GetMapping
    @Operation(summary = "Buscar no Spotify", description = "Realiza uma busca global por músicas, álbuns e artistas no Spotify")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Termo de busca inválido ou vazio")
    })
    public ResponseEntity<SpotifySearchResponseDTO> search(
            @RequestParam(name = "q") String query
    ) {
        SpotifySearchResponseDTO results = searchService.search(query);
        return ResponseEntity.ok(results);
    }
}