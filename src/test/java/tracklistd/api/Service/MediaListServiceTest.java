package tracklistd.api.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tracklistd.api.Entity.Album;
import tracklistd.api.Entity.Enums.ListType;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.MediaExceptions.MediaException;
import tracklistd.api.Exceptions.MediaListExceptions.ListNameBlankException;
import tracklistd.api.Exceptions.MediaListExceptions.MediaListNameAlreadyExitsException;
import tracklistd.api.Exceptions.MediaListExceptions.MediaListaException;
import tracklistd.api.Exceptions.MediaListExceptions.MediaListaOwnershipViolation;
import tracklistd.api.Exceptions.MediaListExceptions.InvalidMediaTypeForListException;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Repository.MediaListRepository;
import tracklistd.api.Repository.MediaRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaListServiceTest {

    @Mock
    private MediaListRepository mediaListRepository;

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaListService mediaListService;

    private User author;
    private MediaList mediaList;

    @BeforeEach
    void setUp() {
        // Configura autor padrão para os testes
        author = new User();
        author.setId(1L);

        // Configura lista padrão para os testes
        mediaList = new MediaList(author, ListType.MUSIC, "Minha Playlist", Privacy.PUBLIC, false);
        ReflectionTestUtils.setField(mediaList, "id", 10L);
    }

    // --- Testes de createMediaList ---

    @Test
    void createMediaList_whenListNameIsBlank_shouldThrowListNameBlankException() {
        // Arrange (Configuração)
        String blankName = "   ";

        // Act & Assert (Ação e Validação)
        assertThrows(ListNameBlankException.class,
                () -> mediaListService.createMediaList(author, ListType.MUSIC, blankName, Privacy.PUBLIC, false));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void createMediaList_whenListNameAlreadyExists_shouldThrowMediaListNameAlreadyExitsException() {
        // Arrange (Configuração)
        String listName = "Minha Playlist";
        when(mediaListRepository.findMediaListByAuthorAndListName(author, listName))
                .thenReturn(Optional.of(new MediaList()));

        // Act & Assert (Ação e Validação)
        assertThrows(MediaListNameAlreadyExitsException.class,
                () -> mediaListService.createMediaList(author, ListType.MUSIC, listName, Privacy.PUBLIC, false));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void createMediaList_whenSuccess_shouldReturnMediaListAndCallSave() {
        // Arrange (Configuração)
        String listName = "Nova Playlist";
        when(mediaListRepository.findMediaListByAuthorAndListName(author, listName))
                .thenReturn(Optional.empty());

        // Act (Ação)
        MediaList result = mediaListService.createMediaList(author, ListType.MUSIC, listName, Privacy.PUBLIC, false);

        // Assert (Validação)
        assertNotNull(result);
        assertEquals(author, result.getAuthorPublication());
        assertEquals(ListType.MUSIC, result.getTypeOfList());
        assertEquals(listName, result.getListName());
        assertEquals(Privacy.PUBLIC, result.getWhoCanSee());
        assertFalse(result.getIsFavorite());

        verify(mediaListRepository, times(1)).save(result);
    }

    // --- Testes de renameMediaList ---

    @Test
    void renameMediaList_whenNewNameIsBlank_shouldThrowListNameBlankException() {
        // Arrange (Configuração)
        Long listId = 10L;
        String blankName = "";
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));

        // Act & Assert (Ação e Validação)
        assertThrows(ListNameBlankException.class,
                () -> mediaListService.renameMediaList(blankName, listId, author.getId()));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void renameMediaList_whenNewNameAlreadyExists_shouldThrowMediaListNameAlreadyExitsException() {
        // Arrange (Configuração)
        Long listId = 10L;
        String newName = "Playlist Existente";
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));
        when(mediaListRepository.findMediaListByAuthorAndListName(author, newName))
                .thenReturn(Optional.of(new MediaList()));

        // Act & Assert (Ação e Validação)
        assertThrows(MediaListNameAlreadyExitsException.class,
                () -> mediaListService.renameMediaList(newName, listId, author.getId()));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void renameMediaList_whenListDoesNotExist_shouldThrowMediaListaException() {
        // Arrange (Configuração)
        Long listId = 99L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.empty());

        // Act & Assert (Ação e Validação)

        assertThrows(ResourceNotFoundException.class,
                () -> mediaListService.renameMediaList("Novo Nome", listId, author.getId()));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void renameMediaList_whenUserIsNotOwner_shouldThrowMediaListaOwnershipViolation() {
        // Arrange (Configuração)
        Long listId = 10L;
        Long otherUserId = 999L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));

        // Act & Assert (Ação e Validação)
        assertThrows(MediaListaOwnershipViolation.class,
                () -> mediaListService.renameMediaList("Novo Nome", listId, otherUserId));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void renameMediaList_whenSuccess_shouldCallSave() {
        // Arrange (Configuração)
        Long listId = 10L;
        String newName = "Playlist Renovada";
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));
        when(mediaListRepository.findMediaListByAuthorAndListName(author, newName))
                .thenReturn(Optional.empty());

        // Act (Ação)
        mediaListService.renameMediaList(newName, listId, author.getId());

        // Assert (Validação)
        assertEquals(newName, mediaList.getListName());
        verify(mediaListRepository, times(1)).save(mediaList);
    }

    // --- Testes de deleteMediaList ---

    @Test
    void deleteMediaList_whenListDoesNotExist_shouldThrowMediaListaException() {
        // Arrange (Configuração)
        Long listId = 99L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.empty());

        // Act & Assert (Ação e Validação)

        assertThrows(ResourceNotFoundException.class, () -> mediaListService.deleteMediaList(listId, author.getId()));
        verify(mediaListRepository, never()).delete(any(MediaList.class));
    }

    @Test
    void deleteMediaList_whenUserIsNotOwner_shouldThrowMediaListaOwnershipViolation() {
        // Arrange (Configuração)
        Long listId = 10L;
        Long otherUserId = 999L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));

        // Act & Assert (Ação e Validação)
        assertThrows(MediaListaOwnershipViolation.class, () -> mediaListService.deleteMediaList(listId, otherUserId));
        verify(mediaListRepository, never()).delete(any(MediaList.class));
    }

    @Test
    void deleteMediaList_whenSuccess_shouldCallDelete() {
        // Arrange (Configuração)
        Long listId = 10L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));

        // Act (Ação)
        mediaListService.deleteMediaList(listId, author.getId());

        // Assert (Validação)
        verify(mediaListRepository, times(1)).delete(mediaList);
    }

    // --- Testes de addMediaToList ---

    @Test
    void addMediaToList_whenListDoesNotExist_shouldThrowMediaListaException() {
        // Arrange (Configuração)
        Long listId = 99L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.empty());

        // Act & Assert (Ação e Validação)

        assertThrows(ResourceNotFoundException.class,
                () -> mediaListService.addMediaToList(listId, 20L, author.getId()));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void addMediaToList_whenUserIsNotOwner_shouldThrowMediaListaOwnershipViolation() {
        // Arrange (Configuração)
        Long listId = 10L;
        Long otherUserId = 999L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));

        // Act & Assert (Ação e Validação)
        assertThrows(MediaListaOwnershipViolation.class,
                () -> mediaListService.addMediaToList(listId, 20L, otherUserId));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void addMediaToList_whenMediaDoesNotExist_shouldThrowMediaException() {
        // Arrange (Configuração)
        Long listId = 10L;
        Long mediaId = 99L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());

        // Act & Assert (Ação e Validação)

        assertThrows(ResourceNotFoundException.class,
                () -> mediaListService.addMediaToList(listId, mediaId, author.getId()));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void addMediaToList_whenMediaTypeDoesNotMatch_shouldThrowMediaException() {
        // Arrange (Configuração)
        Long listId = 10L;
        Long mediaId = 20L;
        // Lista é de MÚSICA, mas passamos um ÁLBUM
        Album album = new Album();
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(album));

        // Act & Assert (Ação e Validação)
        assertThrows(InvalidMediaTypeForListException.class,
                () -> mediaListService.addMediaToList(listId, mediaId, author.getId()));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void addMediaToList_whenSuccess_shouldCallSave() {
        // Arrange (Configuração)
        Long listId = 10L;
        Long mediaId = 20L;
        Music music = new Music();
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(music));

        // Act (Ação)
        mediaListService.addMediaToList(listId, mediaId, author.getId());

        // Assert (Validação)
        assertTrue(mediaList.getMedia().contains(music));
        verify(mediaListRepository, times(1)).save(mediaList);
    }

    // --- Testes de removeMediaFromList ---

    @Test
    void removeMediaFromList_whenListDoesNotExist_shouldThrowMediaListaException() {
        // Arrange (Configuração)
        Long listId = 99L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.empty());

        // Act & Assert (Ação e Validação)

        assertThrows(ResourceNotFoundException.class,
                () -> mediaListService.removeMediaFromList(listId, 20L, author.getId()));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void removeMediaFromList_whenUserIsNotOwner_shouldThrowMediaListaOwnershipViolation() {
        // Arrange (Configuração)
        Long listId = 10L;
        Long otherUserId = 999L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));

        // Act & Assert (Ação e Validação)
        assertThrows(MediaListaOwnershipViolation.class,
                () -> mediaListService.removeMediaFromList(listId, 20L, otherUserId));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void removeMediaFromList_whenMediaIsNotInList_shouldThrowMediaException() {
        // Arrange (Configuração)
        Long listId = 10L;
        Long mediaId = 20L;
        Music musicNotInList = new Music();
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(musicNotInList));

        // Act & Assert (Ação e Validação)

        assertThrows(ResourceNotFoundException.class,
                () -> mediaListService.removeMediaFromList(listId, mediaId, author.getId()));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void removeMediaFromList_whenSuccess_shouldCallSave() {
        // Arrange (Configuração)
        Long listId = 10L;
        Long mediaId = 20L;
        Music music = new Music();
        mediaList.addMedia(music); // Adiciona na lista antes
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(music));

        // Act (Ação)
        mediaListService.removeMediaFromList(listId, mediaId, author.getId());

        // Assert (Validação)
        assertFalse(mediaList.getMedia().contains(music));
        verify(mediaListRepository, times(1)).save(mediaList);
    }

    // --- Testes de favoriteMediaList ---

    @Test
    void favoriteMediaList_whenListDoesNotExist_shouldThrowMediaListaException() {
        // Arrange (Configuração)
        Long listId = 99L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.empty());

        // Act & Assert (Ação e Validação)

        assertThrows(ResourceNotFoundException.class, () -> mediaListService.favoriteMediaList(listId));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void favoriteMediaList_whenSuccess_shouldSetFavoriteTrueAndCallSave() {
        // Arrange (Configuração)
        Long listId = 10L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));

        // Act (Ação)
        mediaListService.favoriteMediaList(listId);

        // Assert (Validação)
        assertTrue(mediaList.getIsFavorite());
        verify(mediaListRepository, times(1)).save(mediaList);
    }

    // --- Testes de unfavoriteMediaList ---

    @Test
    void unfavoriteMediaList_whenListDoesNotExist_shouldThrowMediaListaException() {
        // Arrange (Configuração)
        Long listId = 99L;
        when(mediaListRepository.findById(listId)).thenReturn(Optional.empty());

        // Act & Assert (Ação e Validação)

        assertThrows(ResourceNotFoundException.class, () -> mediaListService.unfavoriteMediaList(listId));
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void unfavoriteMediaList_whenSuccess_shouldSetFavoriteFalseAndCallSave() {
        // Arrange (Configuração)
        Long listId = 10L;
        mediaList.setFavorite(true); // Começa como favorita
        when(mediaListRepository.findById(listId)).thenReturn(Optional.of(mediaList));

        // Act (Ação)
        mediaListService.unfavoriteMediaList(listId);

        // Assert (Validação)
        assertFalse(mediaList.getIsFavorite());
        verify(mediaListRepository, times(1)).save(mediaList);
    }
}