package tracklistd.api.Service;

import org.springframework.stereotype.Service;
import tracklistd.api.Entity.Media;
import tracklistd.api.Exceptions.MediaExceptions.MediaException;
import tracklistd.api.Repository.MediaRepository;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository)
    {
        this.mediaRepository = mediaRepository;
    }

    public Media getMediaById(String mediaId)
    {
        Media media = this.mediaRepository.findMediaBySpotifyID(mediaId).orElseThrow(
                () -> new MediaException("Essa midia não existe")
        );
        return media;
    }
}
