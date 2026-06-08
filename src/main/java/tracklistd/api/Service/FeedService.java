package tracklistd.api.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;
import tracklistd.api.Repository.PublicationRepository;

@Service
public class FeedService {

    private final PublicationRepository publicationRepository;

    public FeedService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    public List<Publication> getSocialFeed(User user) {

        Set<User> following = user.getFollowing(); // pega todos os seguidos do usuario

        return publicationRepository
                .findByAuthorInOrderByCreatedAtDesc(following); // retorna todos os posts da lista "seguindo" do usuario
    }

    public List<Publication> getGlobalFeed() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        return publicationRepository.findTrending(oneWeekAgo);
    }
}
