package tracklistd.api.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tracklistd.api.Dto.SpotifyAPI.SpotifyAlbumResponseDTO;
import tracklistd.api.Dto.SpotifyAPI.SpotifyArtistResponseDTO;
import tracklistd.api.Entity.Album;
import tracklistd.api.Entity.Artist;
import tracklistd.api.Integration.Spotify.Client.SpotifyClient;
import tracklistd.api.Mapper.SpotifyEntityMapper;
import tracklistd.api.Repository.ArtistRepository;
import tracklistd.api.Repository.MediaRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock private ArtistRepository artistRepository;
    @Mock private MediaRepository mediaRepository;
    @Mock private SpotifyClient spotifyClient;
    @Mock private SpotifyEntityMapper mapper;

    @InjectMocks
    private ArtistService artistService;

    private Artist artistEntity;
    private SpotifyArtistResponseDTO spotifyArtistDto;
    private SpotifyAlbumResponseDTO spotifyAlbumDto;

    private final String SPOTIFY_ID = "spotify-id-123"; 

    @BeforeEach
    void setUp() {
        artistEntity = new Artist(); 
        artistEntity.setSpotifyID(SPOTIFY_ID); 
        artistEntity.setName("The Beatles");

        spotifyArtistDto = mock(SpotifyArtistResponseDTO.class);

        spotifyAlbumDto = mock(SpotifyAlbumResponseDTO.class);
        when(spotifyAlbumDto.id()).thenReturn("album-id-456"); 
        when(spotifyAlbumDto.albumType()).thenReturn("album");
    }

    @Test
    void syncArtistAndMedia_whenArtistDoesNotExist_shouldCreateArtistAndSyncMedia() {
        // Arrange
        Album fakeAlbumEntity = new Album();
        
        when(spotifyClient.getArtistById(SPOTIFY_ID)).thenReturn(spotifyArtistDto);
        when(artistRepository.findArtistBySpotifyID(SPOTIFY_ID)).thenReturn(Optional.empty());
        when(mapper.toArtistEntity(spotifyArtistDto)).thenReturn(artistEntity);
        when(artistRepository.save(any(Artist.class))).thenReturn(artistEntity);
        
        when(spotifyClient.getArtistAlbums(SPOTIFY_ID)).thenReturn(List.of(spotifyAlbumDto));
        
        // Ajustado para buscar usando a String "album-id-456"
        when(mediaRepository.findMediaBySpotifyID("album-id-456")).thenReturn(Optional.empty());
        when(mapper.toAlbumEntity(eq(spotifyAlbumDto), anyList())).thenReturn(fakeAlbumEntity);

        // Act
        Artist result = artistService.syncArtistAndMedia(SPOTIFY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(SPOTIFY_ID, result.getSpotifyID()); // Verifica se o ID String foi preservado
        verify(artistRepository, times(1)).save(any(Artist.class));
        verify(mediaRepository, times(1)).save(fakeAlbumEntity);
    }
}