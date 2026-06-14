package tracklistd.api.Service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tracklistd.api.Dto.Like.LikeResponseDTO;
import tracklistd.api.Entity.Like;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;
import tracklistd.api.Repository.LikeRepository;
import tracklistd.api.Repository.PublicationRepository;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PublicationRepository publicationRepository;

    public LikeService(
            LikeRepository likeRepository,
            PublicationRepository publicationRepository) {
        this.likeRepository = likeRepository;
        this.publicationRepository = publicationRepository;
    }

    @Transactional
    public LikeResponseDTO toggleLike(User user, Long publicationId) {
        boolean liked = likeRepository.existsByUserIdAndPublicationId(
                user.getId(),
                publicationId);

        if (liked) {
            likeRepository.deleteByUserIdAndPublicationId(
                    user.getId(),
                    publicationId);
            liked = false;
        } else {
            Publication publication = publicationRepository.findById(publicationId).orElseThrow();

            Like like = new Like();
            like.setUser(user);
            like.setPublication(publication);

            likeRepository.save(like);
            liked = true;
        }

        Long likesCount = likeRepository.countByPublicationId(publicationId);

        return new LikeResponseDTO(
                liked,
                likesCount);
    }
}