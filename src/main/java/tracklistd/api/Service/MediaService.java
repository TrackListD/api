package tracklistd.api.Service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tracklistd.api.Entity.Media;
import tracklistd.api.Exceptions.ResourceNotFoundException;
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
                () -> new ResourceNotFoundException("Essa midia não existe")
        );
        return media;
    }

    @Transactional
    public Media getOrCreateBySpotifyId(String spotifyId, String mediaType) {
        
        // 1. Tenta achar no MySQL local primeiro
        return mediaRepository.findMediaBySpotifyID(spotifyId)
                .orElseGet(() -> fabricarSalvarNovaMedia(spotifyId, mediaType));
    }
}
