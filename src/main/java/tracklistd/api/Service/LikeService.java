/*
 * package tracklistd.api.Service;
 * 
 * import tracklistd.api.Dto.LikeResponseDTO;
 * import tracklistd.api.Entity.Like;
 * import tracklistd.api.Entity.Publication;
 * import tracklistd.api.Entity.User;
 * import tracklistd.api.Repository.LikeRepository;
 * import tracklistd.api.Repository.PublicationRepository;
 * 
 * public class LikeService {
 * 
 * private final LikeRepository likeRepository;
 * private final PublicationRepository publicationRepository;
 * 
 * public LikeService(LikeRepository likeRepository, PublicationRepository
 * publicationRepository) {
 * this.likeRepository = likeRepository;
 * this.publicationRepository = publicationRepository;
 * }
 * 
 * public LikeResponseDTO toggleLike(User user, Long publicationId) {
 * boolean liked = likeRepository.existsByUserIdAndPublicationId(
 * user.getId(),
 * publicationId
 * 
 * )
 * 
 * if(!liked){
 * likeRepository.deleteByUserIdAndPublicationId(
 * user.getId(),
 * publicationId)
 * 
 * } else {
 * Publication publication = publicationRepository.findById(publicationId);
 * Like like = new Like();
 * like.setUser(user);
 * like.setPublication(publication);
 * likeRepository.save(like);
 * }
 * Integer likesCount = likeRepository.countByPublicationId(publicationId);
 * return new LikeResponseDTO(
 * liked,
 * likesCount
 * )
 * }
 * }
 */