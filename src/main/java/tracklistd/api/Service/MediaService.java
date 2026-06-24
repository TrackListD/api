package tracklistd.api.Service;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import tracklistd.api.Dto.SpotifyAPI.SpotifyAlbumResponseDTO;
import tracklistd.api.Dto.SpotifyAPI.SpotifyArtistResponseDTO;
import tracklistd.api.Entity.Album;
import tracklistd.api.Entity.Artist;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Music;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Integration.Spotify.Client.SpotifyClient;
import tracklistd.api.Mapper.SpotifyEntityMapper;
import tracklistd.api.Repository.ArtistRepository;
import tracklistd.api.Repository.MediaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private final SpotifyClient spotifyClient;
    private final ArtistRepository artistRepository;
    private final SpotifyEntityMapper mapper;

    public Media getMediaById(String mediaId) {
        return mediaRepository.findMediaBySpotifyID(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Essa mídia não existe"));
    }

    // Cria um álbum e salva no banco de dados a partir de ume resposta da api do spotify
    @Transactional
    public Album createAlbumFromSpotify(String spotifyId) {
        SpotifyAlbumResponseDTO albumDto = spotifyClient.getAlbumById(spotifyId);

        List<Artist> resolvedArtists = albumDto.artists().stream()
            .map((SpotifyArtistResponseDTO artistDto) -> {
                return artistRepository.findArtistBySpotifyID(artistDto.id())
                    .orElseGet(() -> artistRepository.save(mapper.toArtistEntity(artistDto)));
            }).toList();

        Album album = mapper.toAlbumEntity(albumDto, resolvedArtists);
        List<Music> musicEntities = albumDto.tracks().items().stream()
            .map(musicDto -> mapper.toMusicEntity(musicDto, resolvedArtists))
            .toList();

        musicEntities.forEach(album::addMusic);
        
        musicEntities.forEach(m -> m.setAlbum(album));

        return mediaRepository.save(album);
    }
}