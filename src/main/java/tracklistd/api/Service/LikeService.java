package tracklistd.api.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tracklistd.api.Dto.Like.LikeResponseDTO;
import tracklistd.api.Dto.User.UserMinResponseDTO;
import tracklistd.api.Entity.Like;
import tracklistd.api.Entity.Publication;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.PublicationExceptions.PublicationDoesNotExist;
import tracklistd.api.Mapper.LikeMapper;
import tracklistd.api.Repository.LikeRepository;
import tracklistd.api.Repository.PublicationRepository;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PublicationRepository publicationRepository;
    private final LikeMapper likeMapper;

    public LikeService(
            LikeRepository likeRepository,
            PublicationRepository publicationRepository, LikeMapper likeMapper) {
        this.likeRepository = likeRepository;
        this.publicationRepository = publicationRepository;
        this.likeMapper = likeMapper;
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

    @Transactional
    public List<UserMinResponseDTO> getWhoLiked(Long publicationId) {
        if (!publicationRepository.existsById(publicationId)) {
            throw new PublicationDoesNotExist(publicationId);
        }

        List<User> users = likeRepository.findUsersByPublicationId(publicationId);
        return likeMapper.toUserMinDTOList(users);
    }
}