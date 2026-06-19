package tracklistd.api.Service;

import org.springframework.stereotype.Service;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Repository.PublicationRepository;

@Service
public class PublicationService {

    private final PublicationRepository publicationRepository;

    public PublicationService(PublicationRepository publicationRepository)
    {
        this.publicationRepository = publicationRepository;
    }

    public Publication getPublicationById(Long idPost)
    {
        return this.publicationRepository.findById(idPost).orElseThrow(
                () -> new ResourceNotFoundException("Publicação não Encontrada!")
        );
    }
}
