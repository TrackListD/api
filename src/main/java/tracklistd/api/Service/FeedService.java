/*
 * package tracklistd.api.Service;
 * 
 * import java.util.List;
 * 
 * import tracklistd.api.Entity.User;
 * import org.springframework.stereotype.Service;
 * 
 * import tracklistd.api.Entity.Publication;
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
 * List<User> following = user.getFollowing();
 * 
 * return publicationRepository.findByAuthorInOrderByCreatedAtDesc(following);
 * 
 * }
 * }
 * 
 * comentei essa parte pq as entidades ainda n tao prontas
 * tentem seguir um pouco do que coloquei aqui, se possivel
 */
