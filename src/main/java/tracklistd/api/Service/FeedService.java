package tracklistd.api.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;
import tracklistd.api.Repository.PublicationRepository;
import tracklistd.api.Repository.UserRepository;

@Service
public class FeedService {

    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;

    public FeedService(PublicationRepository publicationRepository, UserRepository userRepository) {
        this.publicationRepository = publicationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public List<Publication> getSocialFeed(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow();

        return publicationRepository
                .findByAuthorInOrderByPublicationDateDesc(
                        user.getFollowing());
    }

    public List<Publication> getGlobalFeed() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        return publicationRepository.findTrending(oneWeekAgo);
    }
}
