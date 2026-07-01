package tracklistd.api.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tracklistd.api.Dto.SpotifyAPI.SpotifySearchResponseDTO;
import tracklistd.api.Integration.Spotify.Client.SpotifyClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpotifySearchServiceTest {

    @Mock
    private SpotifyClient spotifyClient;

    @InjectMocks
    private SpotifySearchService spotifySearchService;

    private SpotifySearchResponseDTO mockResponse;

    @BeforeEach
    void setUp() {
        // Criamos um mock da resposta para evitar instanciar todos os containers complexos (Tracks, Albums, Artists)
        mockResponse = mock(SpotifySearchResponseDTO.class);
    }

    @Test
    void search_whenQueryIsNull_shouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> spotifySearchService.search(null));
        
        assertEquals("Não é possóvel buscar por uma query vazia", exception.getMessage());
        verify(spotifyClient, never()).search(anyString());
    }

    @Test
    void search_whenQueryIsBlank_shouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> spotifySearchService.search("   "));
        
        assertEquals("Não é possóvel buscar por uma query vazia", exception.getMessage());
        verify(spotifyClient, never()).search(anyString());
    }

    @Test
    void search_whenSuccess_shouldReturnSpotifySearchResponseDTO() {
        // Arrange
        String validQuery = "Arctic Monkeys";
        when(spotifyClient.search(validQuery)).thenReturn(mockResponse);

        // Act
        SpotifySearchResponseDTO result = spotifySearchService.search(validQuery);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(spotifyClient, times(1)).search(validQuery);
    }
}