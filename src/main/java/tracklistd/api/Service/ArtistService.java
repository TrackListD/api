package tracklistd.api.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import tracklistd.api.Dto.Artist.ArtistDetailsResponseDTO;
import tracklistd.api.Dto.SpotifyAPI.SpotifyAlbumResponseDTO;
import tracklistd.api.Dto.SpotifyAPI.SpotifyArtistResponseDTO;
import tracklistd.api.Entity.Album;
import tracklistd.api.Entity.Artist;
import tracklistd.api.Entity.Media;
import tracklistd.api.Integration.Spotify.Client.SpotifyClient;
import tracklistd.api.Mapper.SpotifyEntityMapper;
import tracklistd.api.Repository.AlbumRepository;
import tracklistd.api.Repository.ArtistRepository;
import tracklistd.api.Repository.MediaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final MediaRepository mediaRepository;
    private final AlbumRepository albumRepository;
    private final SpotifyClient spotifyClient;
    private final SpotifyEntityMapper mapper;

    @Transactional
    public Artist syncArtistAndMedia(String spotifyId) {
        System.out.println("DEBUG - O ID que chegou no Service foi: '" + spotifyId + "'");

        SpotifyArtistResponseDTO artistDto = spotifyClient.getArtistById(spotifyId);

        Artist artist = artistRepository.findArtistBySpotifyID(spotifyId)
                .orElseGet(() -> mapper.toArtistEntity(artistDto));

        artist.setProfilePictureURL(artistDto.getProfilePictureURL());
        
        artist = artistRepository.save(artist);

        syncReleasedMedia(artist, spotifyId);

        return artist;
    }

    private void syncReleasedMedia(Artist artist, String spotifyId) {
    List<SpotifyAlbumResponseDTO> artistAlbums = spotifyClient.getArtistAlbums(spotifyId);

    for (SpotifyAlbumResponseDTO albumDto : artistAlbums) {
        Optional<Media> existingMedia = mediaRepository.findMediaBySpotifyID(albumDto.id());
        
        if (existingMedia.isEmpty()) {
            Album newAlbum = mapper.toAlbumEntity(albumDto, List.of(artist));
            newAlbum.setSingle("single".equalsIgnoreCase(albumDto.albumType()));
            newAlbum.setAuthors(List.of(artist));
            
            mediaRepository.save(newAlbum);
            System.out.println("DEBUG - Álbum persistido com ID: " + albumDto.id());
        } else {
            System.out.println("DEBUG - Álbum já existente: " + albumDto.id());
        }
    }
}

    @Transactional()
    public ArtistDetailsResponseDTO getArtistDetails(String spotifyId) {
        Artist artist = syncArtistAndMedia(spotifyId); 

        List<Album> albums = albumRepository.findAlbumsByArtistId(spotifyId);

        return mapper.toArtistsDetailsResponseDTO(artist, albums);
    }
}