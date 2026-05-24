/*
 * package tracklistd.api.Service;
 * 
 * import java.util.List;
 * 
 * import org.springframework.stereotype.Service;
 * 
 * import tracklistd.api.Entity.Publication;
 * import tracklistd.api.Entity.User;
 * import tracklistd.api.Repository.PublicationRepository;
 * 
 * @Service
 * public class FeedService {
 * 
 * private final PublicationRepository publicationRepository;
 * 
 * public FeedService(PublicationRepository publicationRepository) {
 * this.publicationRepository = publicationRepository;
 * }
 * 
 * public List<Publication> getFeed(User user) {
 * 
 * List<User> following = user.getFollowing();
 * 
 * return publicationRepository
 * .findByAuthorInOrderByCreatedAtDesc(following);
 * }
 * }
 */