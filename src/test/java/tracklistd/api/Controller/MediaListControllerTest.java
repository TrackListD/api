package tracklistd.api.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tracklistd.api.Dto.MediaList.MediaListEditRequestDto;
import tracklistd.api.Dto.MediaList.MediaListOwnerResponseDto;
import tracklistd.api.Dto.MediaList.MediaListRequestDto;
import tracklistd.api.Dto.MediaList.MediaListResponseDto;
import tracklistd.api.Entity.Enums.ListType;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.MediaListExceptions.MediaListNameAlreadyExitsException;
import tracklistd.api.Exceptions.MediaListExceptions.MediaListaOwnershipViolation;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Integration.FirebaseAuth.Config.SecurityConfig;
import tracklistd.api.Integration.FirebaseAuth.FirebaseFilter;
import tracklistd.api.Mapper.MediaListMapper;
import tracklistd.api.Service.*;
import tracklistd.api.Dto.Media.MediaMinDTO;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MediaListController.class)
@Import({SecurityConfig.class, FirebaseFilter.class})
public class MediaListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private MediaListService mediaListService;

    @MockitoBean
    private MediaListMapper mediaListMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private FirebaseService firebaseService;

    private User testUser;
    private User otherUser;
    private MediaList testMediaList;
    private MediaListResponseDto testResponseDto;
    private MediaListOwnerResponseDto testOwnerResponseDto;

    private final UsernamePasswordAuthenticationToken mockAuth = new UsernamePasswordAuthenticationToken(
            "test-user", null, Collections.emptyList()
    );

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("User Test");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Other User");

        testMediaList = Mockito.spy(new MediaList(testUser, ListType.ALBUM, "My Album List", Privacy.PUBLIC, false, null, null, null));
        doReturn(10L).when(testMediaList).getId();

        testResponseDto = new MediaListResponseDto(
                10L, ListType.ALBUM, "My Album List", false, 1L, "User Test",
                Set.of(new MediaMinDTO("media-1", "Media Title", "Artist Name", "album", "cover-url", 180000, "3m")),
                180000, "3m", null, null, null
        );

        testOwnerResponseDto = new MediaListOwnerResponseDto(
                testResponseDto, Privacy.PUBLIC
        );
    }

    // --- POST /api/mediaList ---

    @Test
    @DisplayName("createMediaList deve retornar 201 quando dados válidos")
    void createMediaList_deveRetornar201_quandoDadosValidos() throws Exception {
        MediaListRequestDto request = new MediaListRequestDto(ListType.ALBUM, "My Album List", false, Privacy.PUBLIC, null, null, null, null);

        when(mediaListService.createMediaList(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(testMediaList);
        when(mediaListMapper.toResponseDto(testMediaList)).thenReturn(testResponseDto);

        mockMvc.perform(post("/api/mediaList")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.listName").value("My Album List"))
                .andExpect(jsonPath("$.typeOfList").value("ALBUM"));
    }

    @Test
    @DisplayName("createMediaList deve retornar 400 quando nome da lista for em branco")
    void createMediaList_deveRetornar400_quandoNomeDaListaForEmBranco() throws Exception {
        MediaListRequestDto request = new MediaListRequestDto(ListType.ALBUM, "  ", false, Privacy.PUBLIC, null, null, null, null);

        mockMvc.perform(post("/api/mediaList")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("createMediaList deve retornar 409 quando nome da lista já existir")
    void createMediaList_deveRetornar409_quandoNomeDaListaJaExistir() throws Exception {
        MediaListRequestDto request = new MediaListRequestDto(ListType.ALBUM, "My Album List", false, Privacy.PUBLIC, null, null, null, null);

        MediaListNameAlreadyExitsException exception = new MediaListNameAlreadyExitsException("My Album List");
        when(mediaListService.createMediaList(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(exception);

        mockMvc.perform(post("/api/mediaList")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // --- GET /api/mediaList/{id} ---

    @Test
    @DisplayName("getOneListByUser deve retornar 200 Owner quando usuário for dono")
    void getOneListByUser_deveRetornar200Owner_quandoUsuarioForDono() throws Exception {
        when(mediaListService.getMediaListById(10L)).thenReturn(testMediaList);
        when(mediaListMapper.toResponseDto(testMediaList)).thenReturn(testResponseDto);
        when(mediaListMapper.toOwnerResponseDTO(testMediaList, testResponseDto)).thenReturn(testOwnerResponseDto);

        mockMvc.perform(get("/api/mediaList/10")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.whoCanSee").value("PUBLIC"))
                .andExpect(jsonPath("$.publicData.listName").value("My Album List"));
    }

    @Test
    @DisplayName("getOneListByUser deve retornar 200 Public quando usuário não for dono")
    void getOneListByUser_deveRetornar200Public_quandoUsuarioNaoForDono() throws Exception {
        when(mediaListService.getMediaListById(10L)).thenReturn(testMediaList);
        when(mediaListMapper.toResponseDto(testMediaList)).thenReturn(testResponseDto);

        mockMvc.perform(get("/api/mediaList/10")
                .with(authentication(new UsernamePasswordAuthenticationToken(otherUser, null, Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.whoCanSee").doesNotExist())
                .andExpect(jsonPath("$.listName").value("My Album List"));
    }

    @Test
    @DisplayName("getOneListByUser deve retornar 200 Public quando não autenticado")
    void getOneListByUser_deveRetornar200Public_quandoNaoAutenticado() throws Exception {
        when(mediaListService.getMediaListById(10L)).thenReturn(testMediaList);
        when(mediaListMapper.toResponseDto(testMediaList)).thenReturn(testResponseDto);

        mockMvc.perform(get("/api/mediaList/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.whoCanSee").doesNotExist())
                .andExpect(jsonPath("$.listName").value("My Album List"));
    }

    @Test
    @DisplayName("getOneListByUser deve retornar 404 quando lista não encontrada")
    void getOneListByUser_deveRetornar404_quandoListaNaoEncontrada() throws Exception {
        when(mediaListService.getMediaListById(99L)).thenThrow(new ResourceNotFoundException("List not found"));

        mockMvc.perform(get("/api/mediaList/99"))
                .andExpect(status().isNotFound());
    }

    // --- GET /api/mediaList/user/{userId} ---

    @Test
    @DisplayName("getAllListByUser deve retornar todas como Owner quando usuário for dono")
    void getAllListByUser_deveRetornarTodasComoOwner_quandoUsuarioForDono() throws Exception {
        when(userService.findUserById(1L)).thenReturn(testUser);
        when(mediaListService.getAllByUser(testUser)).thenReturn(List.of(testMediaList));
        when(mediaListMapper.toResponseDto(testMediaList)).thenReturn(testResponseDto);
        when(mediaListMapper.toOwnerResponseDTO(testMediaList, testResponseDto)).thenReturn(testOwnerResponseDto);

        mockMvc.perform(get("/api/mediaList/user/1")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].whoCanSee").value("PUBLIC"))
                .andExpect(jsonPath("$[0].publicData.listName").value("My Album List"));
    }

    @Test
    @DisplayName("getAllListByUser deve retornar todas como Public quando usuário não for dono")
    void getAllListByUser_deveRetornarTodasComoPublic_quandoUsuarioNaoForDono() throws Exception {
        when(userService.findUserById(1L)).thenReturn(testUser);
        when(mediaListService.getAllByUser(testUser)).thenReturn(List.of(testMediaList));
        when(mediaListMapper.toResponseDto(testMediaList)).thenReturn(testResponseDto);

        mockMvc.perform(get("/api/mediaList/user/1")
                .with(authentication(new UsernamePasswordAuthenticationToken(otherUser, null, Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].whoCanSee").doesNotExist())
                .andExpect(jsonPath("$[0].listName").value("My Album List"));
    }

    // --- PATCH /api/mediaList/{id}/name ---

    @Test
    @DisplayName("editMediaListName deve retornar 202 quando dono edita")
    void editMediaListName_deveRetornar202_quandoDonoEdita() throws Exception {
        MediaListEditRequestDto.EditNameRequestDto request = new MediaListEditRequestDto.EditNameRequestDto("New List Name");

        when(mediaListService.getMediaListById(10L)).thenReturn(testMediaList);
        when(mediaListMapper.toResponseDto(testMediaList)).thenReturn(testResponseDto);
        when(mediaListMapper.toOwnerResponseDTO(testMediaList, testResponseDto)).thenReturn(testOwnerResponseDto);

        mockMvc.perform(patch("/api/mediaList/10/name")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.whoCanSee").value("PUBLIC"));

        verify(mediaListService).renameMediaList("New List Name", 10L, 1L);
    }

    @Test
    @DisplayName("editMediaListName deve retornar 401 quando não autenticado")
    void editMediaListName_deveRetornar403_quandoNaoAutenticado() throws Exception {
        MediaListEditRequestDto.EditNameRequestDto request = new MediaListEditRequestDto.EditNameRequestDto("New Name");

        mockMvc.perform(patch("/api/mediaList/10/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("editMediaListName deve retornar 403 quando não for dono")
    void editMediaListName_deveRetornar403_quandoNaoForDono() throws Exception {
        MediaListEditRequestDto.EditNameRequestDto request = new MediaListEditRequestDto.EditNameRequestDto("New Name");

        doThrow(new MediaListaOwnershipViolation())
                .when(mediaListService).renameMediaList(anyString(), anyLong(), eq(2L));

        mockMvc.perform(patch("/api/mediaList/10/name")
                .with(authentication(new UsernamePasswordAuthenticationToken(otherUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // --- PATCH /api/mediaList/{id}/privacy ---

    @Test
    @DisplayName("editMediaListPrivacy deve retornar 202 quando dono edita")
    void editMediaListPrivacy_deveRetornar202_quandoDonoEdita() throws Exception {
        MediaListEditRequestDto.EditPrivacyRequestDto request = new MediaListEditRequestDto.EditPrivacyRequestDto(Privacy.PRIVATE);

        when(mediaListService.getMediaListById(10L)).thenReturn(testMediaList);
        when(mediaListMapper.toResponseDto(testMediaList)).thenReturn(testResponseDto);
        when(mediaListMapper.toOwnerResponseDTO(testMediaList, testResponseDto)).thenReturn(testOwnerResponseDto);

        mockMvc.perform(patch("/api/mediaList/10/privacy")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        verify(mediaListService).changeMediaListPrivacy(10L, 1L, Privacy.PRIVATE);
    }

    @Test
    @DisplayName("editMediaListPrivacy deve retornar 400 quando privacidade for nula")
    void editMediaListPrivacy_deveRetornar400_quandoPrivacidadeForNula() throws Exception {
        MediaListEditRequestDto.EditPrivacyRequestDto request = new MediaListEditRequestDto.EditPrivacyRequestDto(null);

        mockMvc.perform(patch("/api/mediaList/10/privacy")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- POST /api/mediaList/{id}/medias/{mediaId} ---

    @Test
    @DisplayName("addMediaToList deve retornar 200 quando dono adiciona")
    void addMediaToList_deveRetornar200_quandoDonoAdiciona() throws Exception {
        when(mediaListService.getMediaListById(10L)).thenReturn(testMediaList);
        when(mediaListMapper.toResponseDto(testMediaList)).thenReturn(testResponseDto);
        when(mediaListMapper.toOwnerResponseDTO(testMediaList, testResponseDto)).thenReturn(testOwnerResponseDto);

        mockMvc.perform(post("/api/mediaList/10/medias/spotify-media-123")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))))
                .andExpect(status().isOk());

        verify(mediaListService).addMediaToList(10L, "spotify-media-123", 1L);
    }

    @Test
    @DisplayName("addMediaToList deve retornar 403 quando não for dono")
    void addMediaToList_deveRetornar403_quandoNaoForDono() throws Exception {
        doThrow(new MediaListaOwnershipViolation())
                .when(mediaListService).addMediaToList(anyLong(), anyString(), eq(2L));

        mockMvc.perform(post("/api/mediaList/10/medias/spotify-media-123")
                .with(authentication(new UsernamePasswordAuthenticationToken(otherUser, null, Collections.emptyList()))))
                .andExpect(status().isForbidden());
    }

    // --- DELETE /api/mediaList/{id}/medias/{mediaId} ---

    @Test
    @DisplayName("removeMediaFromList deve retornar 200 quando dono remove")
    void removeMediaFromList_deveRetornar200_quandoDonoRemove() throws Exception {
        when(mediaListService.getMediaListById(10L)).thenReturn(testMediaList);
        when(mediaListMapper.toResponseDto(testMediaList)).thenReturn(testResponseDto);
        when(mediaListMapper.toOwnerResponseDTO(testMediaList, testResponseDto)).thenReturn(testOwnerResponseDto);

        mockMvc.perform(delete("/api/mediaList/10/medias/spotify-media-123")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))))
                .andExpect(status().isOk());

        verify(mediaListService).removeMediaFromList(10L, "spotify-media-123", 1L);
    }

    // --- POST /api/mediaList/{id}/favorite ---

    @Test
    @DisplayName("favoriteMediaList deve retornar 200 quando autenticado")
    void favoriteMediaList_deveRetornar200_quandoUsuarioAutenticado() throws Exception {
        when(mediaListService.getMediaListById(10L)).thenReturn(testMediaList);
        when(mediaListMapper.toResponseDto(testMediaList)).thenReturn(testResponseDto);
        when(mediaListMapper.toOwnerResponseDTO(testMediaList, testResponseDto)).thenReturn(testOwnerResponseDto);

        mockMvc.perform(post("/api/mediaList/10/favorite")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))))
                .andExpect(status().isOk());

        verify(mediaListService).favoriteMediaList(10L);
    }

    @Test
    @DisplayName("favoriteMediaList deve retornar 401 quando não autenticado")
    void favoriteMediaList_deveRetornar403_quandoNaoAutenticado() throws Exception {
        mockMvc.perform(post("/api/mediaList/10/favorite"))
                .andExpect(status().isUnauthorized());
    }

    // --- DELETE /api/mediaList/{id}/favorite ---

    @Test
    @DisplayName("unfavoriteMediaList deve retornar 200 quando autenticado")
    void unfavoriteMediaList_deveRetornar200_quandoUsuarioAutenticado() throws Exception {
        when(mediaListService.getMediaListById(10L)).thenReturn(testMediaList);
        when(mediaListMapper.toResponseDto(testMediaList)).thenReturn(testResponseDto);
        when(mediaListMapper.toOwnerResponseDTO(testMediaList, testResponseDto)).thenReturn(testOwnerResponseDto);

        mockMvc.perform(delete("/api/mediaList/10/favorite")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))))
                .andExpect(status().isOk());

        verify(mediaListService).unfavoriteMediaList(10L);
    }

    // --- DELETE /api/mediaList/{id} ---

    @Test
    @DisplayName("deleteMediaList deve retornar 204 quando dono deleta")
    void deleteMediaList_deveRetornar204_quandoDonoDeleta() throws Exception {
        mockMvc.perform(delete("/api/mediaList/10")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))))
                .andExpect(status().isNoContent());

        verify(mediaListService).deleteMediaList(10L, 1L);
    }

    @Test
    @DisplayName("deleteMediaList deve retornar 401 quando não autenticado")
    void deleteMediaList_deveRetornar403_quandoNaoAutenticado() throws Exception {
        mockMvc.perform(delete("/api/mediaList/10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("deleteMediaList deve retornar 403 quando não for dono")
    void deleteMediaList_deveRetornar403_quandoNaoForDono() throws Exception {
        doThrow(new MediaListaOwnershipViolation())
                .when(mediaListService).deleteMediaList(10L, 2L);

        mockMvc.perform(delete("/api/mediaList/10")
                .with(authentication(new UsernamePasswordAuthenticationToken(otherUser, null, Collections.emptyList()))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("deleteMediaList deve retornar 404 quando não encontrada")
    void deleteMediaList_deveRetornar404_quandoListaNaoEncontrada() throws Exception {
        doThrow(new ResourceNotFoundException("List not found"))
                .when(mediaListService).deleteMediaList(99L, 1L);

        mockMvc.perform(delete("/api/mediaList/99")
                .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))))
                .andExpect(status().isNotFound());
    }
}
