package tracklistd.api.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tracklistd.api.Dto.Comment.CommentResponseDto;
import tracklistd.api.Dto.MediaList.MediaListEditRequestDto;
import tracklistd.api.Dto.MediaList.MediaListOwnerResponseDto;
import tracklistd.api.Dto.MediaList.MediaListRequestDto;
import tracklistd.api.Dto.MediaList.MediaListResponseDto;
import tracklistd.api.Dto.Rating.RatingOwnerResponseDto;
import tracklistd.api.Dto.Rating.RatingResponseDto;
import tracklistd.api.Entity.*;
import tracklistd.api.Entity.Enums.ListType;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Mapper.MediaListMapper;
import tracklistd.api.Service.MediaListService;
import tracklistd.api.Service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/mediaList")
@RequiredArgsConstructor
@Tag(name = "Media Lists", description = "Endpoints para criação e gerenciamento de listas de mídias (playlists de álbuns ou músicas)")
public class MediaListController {

    private final MediaListService mediaListService;
    private final MediaListMapper mediaListMapper;
    private final UserService userService;


    @PostMapping
    @Operation(summary = "Criar lista de mídias", description = "Cria uma nova lista de mídias (músicas ou álbuns)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lista criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "409", description = "Nome de lista já existente para este usuário")
    })
    public ResponseEntity<MediaListResponseDto> createMediaList(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid MediaListRequestDto mediaListRequestDto
    ) {
        ListType typeOfList = mediaListRequestDto.typeOfList();

        String listName = mediaListRequestDto.listName();

        Privacy privacy = mediaListRequestDto.whoCanSee();

        boolean isFavorite = mediaListRequestDto.isFavorite();

        String description = mediaListRequestDto.description();

        String coverImageUrl = mediaListRequestDto.coverImageUrl();

        Set<String> tags = mediaListRequestDto.tags();

        MediaList mediaList = mediaListService.createMediaList(user, typeOfList, listName, privacy, isFavorite, description, coverImageUrl, tags);

        MediaListResponseDto mediaListResponseDto = this.mediaListMapper.toResponseDto(mediaList);

        return ResponseEntity.status(HttpStatus.CREATED).body(mediaListResponseDto);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar lista por ID", description = "Obtém informações públicas ou completas (se for o dono) de uma lista de mídias")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Lista não encontrada")
    })
    public ResponseEntity<?> getOneListByUser(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        MediaList mediaList = this.mediaListService.getMediaListById(id);

        MediaListResponseDto responseDto = this.mediaListMapper.toResponseDto(mediaList);

        if (user == null)
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);

        boolean isOwner = Objects.equals(mediaList.getAuthorPublication().getId(), user.getId());

        if (!isOwner)
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);

        MediaListOwnerResponseDto ownerResponse = this.mediaListMapper.toOwnerResponseDTO(mediaList, responseDto);

        return ResponseEntity.status(HttpStatus.OK).body(ownerResponse);
    }


    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar listas por usuário", description = "Retorna todas as listas de mídias de um determinado usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listas recuperadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<?>> getAllListByUser(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId
    ) {
        User userWanted = this.userService.findUserById(userId);

        List<MediaList> allList = this.mediaListService.getAllByUser(userWanted);

        List<MediaListResponseDto> responseDto = this.buildMediaListResponseList(allList);

        if (user == null)
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);

        boolean isOwner = Objects.equals(userWanted.getId(), user.getId());

        if (!isOwner)
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);

        List<MediaListOwnerResponseDto> listOwnerResponse = new ArrayList<>();

        for (int i = 0; i < allList.size(); i++) {
            listOwnerResponse.add(this.mediaListMapper.toOwnerResponseDTO(allList.get(i), responseDto.get(i)));
        }

        return ResponseEntity.status(HttpStatus.OK).body(listOwnerResponse);
    }


    @PatchMapping("/{id}/name")
    @Operation(summary = "Renomear lista", description = "Permite alterar o nome de uma lista de mídias")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Nome alterado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é o dono do recurso)"),
            @ApiResponse(responseCode = "404", description = "Lista não encontrada")
    })
    public ResponseEntity<MediaListOwnerResponseDto> editMediaListName(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid MediaListEditRequestDto.EditNameRequestDto editNameDto
    ) {

        this.mediaListService.renameMediaList(editNameDto.newName(), id, user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));
    }


    @PatchMapping("/{id}/privacy")
    @Operation(summary = "Alterar privacidade da lista", description = "Altera as configurações de privacidade de uma lista")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Privacidade alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Privacidade inválida fornecida"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é o dono do recurso)"),
            @ApiResponse(responseCode = "404", description = "Lista não encontrada")
    })
    public ResponseEntity<MediaListOwnerResponseDto> editMediaListPrivacy(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid MediaListEditRequestDto.EditPrivacyRequestDto editNameDto
    ) {

        this.mediaListService.changeMediaListPrivacy(id, user.getId(), editNameDto.newPrivacy());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));
    }

    @PatchMapping("/{id}/description")
    public ResponseEntity<MediaListOwnerResponseDto> editMediaListDescription
            (
                    @AuthenticationPrincipal User user,
                    @PathVariable Long id,
                    @RequestBody @Valid MediaListEditRequestDto.EditDescriptionRequestDto editDescriptionDto
            )
    {
        this.mediaListService.changeMediaListDescription(id, user.getId(), editDescriptionDto.newDescription());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));

    }

    @PatchMapping("/{id}/coverImage")
    public ResponseEntity<MediaListOwnerResponseDto> editMediaListcoverImage
            (
                    @AuthenticationPrincipal User user,
                    @PathVariable Long id,
                    @RequestBody @Valid MediaListEditRequestDto.EditCoverImageRequestDto editCoverImageRequestDto
            )
    {
        this.mediaListService.changeMediaListCoverImage(id, user.getId(), editCoverImageRequestDto.newCoverImageUrl());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));

    }

    @PatchMapping("/{id}/tags")
    public ResponseEntity<MediaListOwnerResponseDto> editMediaListTags
            (
                    @AuthenticationPrincipal User user,
                    @PathVariable Long id,
                    @RequestBody @Valid MediaListEditRequestDto.EditTagsRequestDto editTagsDto
            )
    {
        this.mediaListService.updateTagToList(id, user.getId(), editTagsDto.newTags());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));

    }


    @PostMapping("/{id}/medias/{mediaId}")
    @Operation(summary = "Adicionar mídia à lista", description = "Adiciona uma música ou álbum à lista especificada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mídia adicionada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é o dono do recurso)"),
            @ApiResponse(responseCode = "404", description = "Lista ou mídia não encontrada")
    })
    public ResponseEntity<MediaListOwnerResponseDto> addMediaToList(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @PathVariable String mediaId
    ) {

        this.mediaListService.addMediaToList(id, mediaId, user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(buildOwnerResponse(id));
    }


    @DeleteMapping("/{id}/medias/{mediaId}")
    @Operation(summary = "Remover mídia da lista", description = "Remove uma música ou álbum de uma lista existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mídia removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é o dono do recurso)"),
            @ApiResponse(responseCode = "404", description = "Lista ou mídia não encontrada")
    })
    public ResponseEntity<MediaListOwnerResponseDto> removeMediaFromList(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @PathVariable String mediaId
    ) {

        this.mediaListService.removeMediaFromList(id, mediaId, user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(buildOwnerResponse(id));
    }


    @PostMapping("/{id}/favorite")
    @Operation(summary = "Favoritar lista", description = "Adiciona a lista de mídias aos favoritos do usuário logado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista favoritada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é o dono do recurso)"),
            @ApiResponse(responseCode = "404", description = "Lista não encontrada")
    })
    public ResponseEntity<?> favoriteMediaList(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {

        this.mediaListService.favoriteMediaList(id);

        return returnResponseDto(id, user);
    }


    @DeleteMapping("/{id}/favorite")
    @Operation(summary = "Desfavoritar lista", description = "Remove a lista de mídias dos favoritos do usuário logado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista desfavoritada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é o dono do recurso)"),
            @ApiResponse(responseCode = "404", description = "Lista não encontrada")
    })
    public ResponseEntity<?> unfavoriteMediaList(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {

        this.mediaListService.unfavoriteMediaList(id);

        return returnResponseDto(id, user);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir lista", description = "Remove permanentemente a lista de mídias especificada")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lista excluída com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é o dono do recurso)"),
            @ApiResponse(responseCode = "404", description = "Lista não encontrada")
    })
    public ResponseEntity<Void> deleteMediaList(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {

        this.mediaListService.deleteMediaList(id, user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ------- MÉTODOS PRIVADOS

    @NonNull
    private MediaListOwnerResponseDto buildOwnerResponse(Long mediaId) {
        MediaList mediaList = this.mediaListService.getMediaListById(mediaId);

        MediaListResponseDto responseDto = this.mediaListMapper.toResponseDto(mediaList);

        return this.mediaListMapper.toOwnerResponseDTO(mediaList, responseDto);
    }

    @NonNull
    private ResponseEntity<?> returnResponseDto(Long mediaId, User user) {
        MediaList mediaList = this.mediaListService.getMediaListById(mediaId);

        MediaListResponseDto responseDto = this.mediaListMapper.toResponseDto(mediaList);

        boolean isOwner = Objects.equals(mediaList.getAuthorPublication().getId(), user.getId());

        if (!isOwner)
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);

        return ResponseEntity.status(HttpStatus.OK).body(this.mediaListMapper.toOwnerResponseDTO(mediaList, responseDto));
    }

    @NonNull
    private List<MediaListResponseDto> buildMediaListResponseList(List<MediaList> allMediaList) {
        List<MediaListResponseDto> listResponseDtos = new ArrayList<>();
        for (MediaList mediaList : allMediaList) {
            MediaListResponseDto responseDto = this.mediaListMapper.toResponseDto(mediaList);
            listResponseDtos.add(responseDto);
        }

        return listResponseDtos;
    }
}
