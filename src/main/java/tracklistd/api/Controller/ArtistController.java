package tracklistd.api.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tracklistd.api.Dto.Artist.ArtistDetailsResponseDTO;
import tracklistd.api.Service.ArtistService;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
@Tag(name = "Artists", description = "Endpoints para visualização e sincronização de artistas")
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping("/{spotifyId}")
    @Operation(
        summary = "Obter Detalhes do Artista", 
        description = "Sincroniza os dados e a discografia do artista no banco e retorna os detalhes completos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes do artista e álbuns retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado no sistema ou no Spotify")
    })
    public ResponseEntity<ArtistDetailsResponseDTO> getArtistDetails(@PathVariable String spotifyId) {
        ArtistDetailsResponseDTO details = artistService.getArtistDetails(spotifyId);
        System.out.println("DEBUG - O ID recebido no Controller foi: " + spotifyId);
        
        return ResponseEntity.ok(details);
    }
}