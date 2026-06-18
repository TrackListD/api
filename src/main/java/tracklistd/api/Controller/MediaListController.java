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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/mediaList")
@RequiredArgsConstructor
public class MediaListController {


    private final MediaListService mediaListService;
    private final MediaListMapper mediaListMapper;
    private final UserService userService;

//   criar
    @PostMapping()
    public ResponseEntity<MediaListResponseDto> createMediaList
    (
            @AuthenticationPrincipal User user,
            @RequestBody @Valid MediaListRequestDto mediaListRequestDto
    )
    {

        ListType typeOfList = mediaListRequestDto.typeOfList();

        String listName = mediaListRequestDto.listName();

        Privacy privacy = mediaListRequestDto.whoCanSee();

        boolean isFavorite = mediaListRequestDto.isFavorite();

        MediaList mediaList = mediaListService.createMediaList(user,typeOfList,listName, privacy,isFavorite);

        MediaListResponseDto mediaListResponseDto = this.mediaListMapper.toResponseDto(mediaList);

        return ResponseEntity.status(HttpStatus.CREATED).body(mediaListResponseDto);

    }

//  buscar uma lista (owner vs. público)
    @GetMapping("/{id}")
    public ResponseEntity<?> getOneListByUser
    (
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    )
    {

        MediaList mediaList = this.mediaListService.getMediaListById(id);

        MediaListResponseDto responseDto = this.mediaListMapper.toResponseDto(mediaList);

        if(user == null)
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);

        boolean isOwner = Objects.equals(mediaList.getAuthorPublication().getId(), user.getId());

        if(!isOwner)
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);

        MediaListOwnerResponseDto ownerResponse = this.mediaListMapper.toOwnerResponseDTO(mediaList, responseDto);

        return ResponseEntity.status(HttpStatus.OK).body(ownerResponse);

    }

//   listar listas de um usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<?>> getAllListByUser
    (
            @AuthenticationPrincipal User user,
            @PathVariable Long userId
    )
    {

        User userWanted = this.userService.findUserById(userId);

        List<MediaList> allList = this.mediaListService.getAllByUser(userWanted);

        List<MediaListResponseDto> responseDto = this.buildMediaListResponseList(allList);

        if(user == null)
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);

        boolean isOwner = Objects.equals(userWanted.getId(), user.getId());


        if(!isOwner)
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);

        List<MediaListOwnerResponseDto> listOwnerResponse = new ArrayList<>();

        for (int i = 0; i < allList.size(); i++)
        {
            listOwnerResponse.add(this.mediaListMapper.toOwnerResponseDTO(allList.get(i), responseDto.get(i)));
        }

        return ResponseEntity.status(HttpStatus.OK).body(listOwnerResponse);
    }

//   editar nome
    @PatchMapping("/{id}/name")
    public ResponseEntity<MediaListOwnerResponseDto> editMediaListName
    (
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid MediaListEditRequestDto.EditNameRequestDto editNameDto
    )
    {
        if(user == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();


        this.mediaListService.renameMediaList(editNameDto.newName(),id, user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));
    }

//    mudar privacidade
    @PatchMapping("/{id}/privacy")
    public ResponseEntity<MediaListOwnerResponseDto> editMediaListPrivacy
            (
                    @AuthenticationPrincipal User user,
                    @PathVariable Long id,
                    @RequestBody @Valid MediaListEditRequestDto.EditPrivacyRequestDto editNameDto
            )
    {
        if(user == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        this.mediaListService.changeMediaListPrivacy(id, user.getId(), editNameDto.newPrivacy());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildOwnerResponse(id));
    }

//  adicionar mídia à lista
    @PostMapping("/{id}/medias/{mediaId}")
    public ResponseEntity<MediaListOwnerResponseDto> addMediaToList
    (
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @PathVariable String mediaId
    )
    {
        if(user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        this.mediaListService.addMediaToList(id, mediaId, user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(buildOwnerResponse(id));
    }

//  remover mídia da lista
    @DeleteMapping("/{id}/medias/{mediaId}")
    public ResponseEntity<MediaListOwnerResponseDto> removeMediaFromList
    (
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @PathVariable String mediaId
    )
    {
        if(user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);


        this.mediaListService.removeMediaFromList(id,mediaId, user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(buildOwnerResponse(id));
    }

//   favoritar
    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> favoriteMediaList
    (
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    )
    {
        if(user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); //Sem login não conseguem interagir, verificar erro, no front deve mostrar a tela para logar

        this.mediaListService.favoriteMediaList(id);

        return returnResponseDto(id, user);

    }

//   desfavoritar
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<?> unfavoriteMediaList
    (
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    )
    {
        if(user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); //Sem login não conseguem interagir, verificar erro, no front deve mostrar a tela para logar

        this.mediaListService.unfavoriteMediaList(id);

        return returnResponseDto(id, user);
    }

//  apagar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMediaList
    (
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    )
    {
        if(user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

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
    private ResponseEntity<?> returnResponseDto(Long mediaId, User user)
    {
        MediaList mediaList = this.mediaListService.getMediaListById(mediaId);

        MediaListResponseDto responseDto = this.mediaListMapper.toResponseDto(mediaList);

        boolean isOwner = Objects.equals(mediaList.getAuthorPublication().getId(), user.getId());

        if(!isOwner)
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);

        return ResponseEntity.status(HttpStatus.OK).body(this.mediaListMapper.toOwnerResponseDTO(mediaList, responseDto));
    }

    @NonNull
    private List<MediaListResponseDto> buildMediaListResponseList(List<MediaList> allMediaList) {

        List<MediaListResponseDto> listResponseDtos = new ArrayList<>();
        for(MediaList mediaList : allMediaList)
        {
            MediaListResponseDto responseDto = this.mediaListMapper.toResponseDto(mediaList);

            listResponseDtos.add(responseDto);
        }

        return listResponseDtos;

    }
}
