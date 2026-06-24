package tracklistd.api.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tracklistd.api.Entity.Artist;
import tracklistd.api.Service.ArtistService;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
@Tag(name = "Artists", description = "Endpoints para visualização e sincronização de artistas")
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping("/{spotifyId}")
    @Operation(summary = "Buscar e Sincronizar Artista", description = "Recupera os dados de um artista e sincroniza sua discografia (mídias lançadas) no banco de dados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artista sincronizado e retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado no Spotify")
    })
    public ResponseEntity<Artist> getAndSyncArtist(@PathVariable String spotifyId) {
        Artist artist = artistService.syncArtistAndMedia(spotifyId);
        return ResponseEntity.ok(artist);
    }
}