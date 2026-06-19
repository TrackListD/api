package tracklistd.api.Service;

import org.springframework.stereotype.Service;
import tracklistd.api.Entity.Enums.ListType;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.CommentExceptions.SelfCommentException;
import tracklistd.api.Exceptions.MediaExceptions.MediaException;
import tracklistd.api.Exceptions.MediaListExceptions.*;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Repository.MediaListRepository;
import tracklistd.api.Repository.MediaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MediaListService {

    //Injeção de Dependencia
    private final MediaListRepository mediaListRepository;
    private final MediaRepository mediaRepository;

    //Construtor gerenciado pelo Spring Boot
    public MediaListService(MediaListRepository mediaListRepository, MediaRepository mediaRepository) {
        this.mediaListRepository = mediaListRepository;
        this.mediaRepository = mediaRepository;
    }


    //Métodos Publicos

    public MediaList createMediaList(User author, ListType typeOfList, String listName, Privacy whoCanSee, Boolean isFavorite)
    {
        if(listName.isBlank())
            throw new ListNameBlankException();

        Boolean result = checkIfListNameAlreadyExits(author, listName);
        if(result)
            throw new MediaListNameAlreadyExitsException(listName);

        MediaList mediaList = new MediaList(author, typeOfList, listName, whoCanSee, isFavorite);
        mediaListRepository.save(mediaList);

        return mediaList;
    }

    public void renameMediaList(String newName, Long mediaListId, Long authorId)
    {
        MediaList mediaList = findMediaListAndValidateOwner(mediaListId, authorId);

        if(newName.isBlank())
            throw new ListNameBlankException();

        Boolean result = checkIfListNameAlreadyExits(mediaList.getAuthorPublication(), newName);
        if(result)
            throw new MediaListNameAlreadyExitsException(newName);

        mediaList.changeListName(newName);
        mediaListRepository.save(mediaList);

    }


    public void changeMediaListPrivacy(Long mediaListId, Long userId, Privacy newPrivacy)
    {
        MediaList mediaList = this.findMediaListAndValidateOwner(mediaListId, userId);

        mediaList.changePrivacy(newPrivacy);
        this.mediaListRepository.save(mediaList);
    }

    public void deleteMediaList(Long mediaListId, Long authorId)
    {
        MediaList mediaList = findMediaListAndValidateOwner(mediaListId, authorId);

        mediaListRepository.delete(mediaList);

    }

    public void addMediaToList(Long mediaListId, String mediaId, Long authorId)
    {
        MediaList mediaList = findMediaListAndValidateOwner(mediaListId, authorId);

        //Procuro a media pelo Id
        Media media = this.mediaRepository.findMediaBySpotifyID(mediaId).orElseThrow(
                () -> new ResourceNotFoundException("Essa midia não existe")
        );
        //Com a midia retornada, verifico se elá é do tipo da lista
        if(!mediaList.getTypeOfList().matches(media))
            throw new InvalidMediaTypeForListException("Impossivel adicionar um(a) " + media.getClass().getSimpleName() + " em uma lista de " + mediaList.getTypeOfList().toString());
        //Se não for, lanço uma exceção
        //Se for, adiciono a lista
        mediaList.addMedia(media);
        this.mediaListRepository.save(mediaList);

    }

    public void removeMediaFromList(Long mediaListId, String mediaId, Long authorId) {
        MediaList mediaList = findMediaListAndValidateOwner(mediaListId, authorId);

        //Procura a media pelo id
        Media media = this.mediaRepository.findMediaBySpotifyID(mediaId).orElseThrow(
                () -> new ResourceNotFoundException("Essa midia não existe")
        );
        //Com a media retornada, verifico se ela está na lista
        if (!mediaList.getMedia().contains(media))
            throw new ResourceNotFoundException("Essa midia não está na lista");
        //Se não estiver, lanço exceção

        //Se estiver removo
        mediaList.removeMedia(media);
        this.mediaListRepository.save(mediaList);

    }

    public void favoriteMediaList(Long mediaListId)
    {
        MediaList mediaList = findMediaList(mediaListId);

        mediaList.setFavorite(true);
        this.mediaListRepository.save(mediaList);
    }

    public void unfavoriteMediaList(Long mediaListId)
    {
        MediaList mediaList = findMediaList(mediaListId);

        mediaList.setFavorite(false);
        this.mediaListRepository.save(mediaList);
    }

    public List<MediaList> getAllByUser(User user)
    {
        return this.mediaListRepository.findAllByAuthor(user);
    }

    public MediaList getOneByUser(Long userId)
    {
        return this.mediaListRepository.findMediaListByAuthor(userId).orElseThrow(
                () -> new ResourceNotFoundException("Esse usuario ainda não criou nenhuma Lista")
        );
    }

    public MediaList getMediaListById(Long mediaId)
    {
        return this.mediaListRepository.findById(mediaId).orElseThrow(
                () -> new ResourceNotFoundException("Essa Lista não existe ou não foi encontrada")
        );
    }

    //Métodos Privados

    private Boolean checkIfListNameAlreadyExits(User author, String listName)
    {
        Optional<MediaList> result = this.mediaListRepository.findMediaListByAuthorAndListName(author, listName);

        return result.isPresent();
    }

    private MediaList findMediaListAndValidateOwner(Long mediaListId, Long authorId)
    {
        //Verifica se a Lista Existe
        MediaList mediaList = this.mediaListRepository.findById(mediaListId).orElseThrow(
                () -> new ResourceNotFoundException("Está Lista não existe")
        );

        //Se encontrada a Lista, verifica se ela pertence ao User logado
        if(!Objects.equals(mediaList.getAuthorPublication().getId(), authorId))
            throw new MediaListaOwnershipViolation();

        return mediaList;
    }

    private MediaList findMediaList(Long mediaListId)
    {
        MediaList mediaList = this.mediaListRepository.findById(mediaListId).orElseThrow(
                () -> new ResourceNotFoundException("Essa Lista não existe")
        );

        return  mediaList;
    }
}
