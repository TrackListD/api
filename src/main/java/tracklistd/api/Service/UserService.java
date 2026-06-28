package tracklistd.api.Service;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.auth.FirebaseToken;

import tracklistd.api.Dto.User.UserRegisterRequestDTO;
import tracklistd.api.Dto.User.UserUpdatePerfilRequestDTO;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Enums.Punishment;
import tracklistd.api.Entity.Album;
import tracklistd.api.Entity.Artist;
import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Exceptions.UserExceptions.FollowYourself;
import tracklistd.api.Exceptions.UserExceptions.FriendDoesNotExist;
import tracklistd.api.Exceptions.UserExceptions.LoginApiAlreadyUsed;
import tracklistd.api.Exceptions.UserExceptions.UserDoesNotExist;
import tracklistd.api.Repository.MediaListRepository;
import tracklistd.api.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MediaListRepository mediaListRepository;

    public UserService(UserRepository userRepository, MediaListRepository mediaListRepository) {
        this.userRepository = userRepository;
        this.mediaListRepository = mediaListRepository;
    }

    @Transactional
    public User register(UserRegisterRequestDTO dto) {
        if (userRepository.existsByIdLoginApi((dto.idLoginApi())))
            throw new LoginApiAlreadyUsed();

        User newUser = new User();
        newUser.setName(dto.name());
        newUser.setIdLoginApi(dto.idLoginApi());
        newUser.setRole(dto.role());
        newUser.setWhoCanComment(dto.whoCanComment());
        newUser.setBio(dto.bio());
        newUser.setProfilePic(dto.profilePic());

        return userRepository.save(newUser);
    }

    @Transactional
    public User perfilUpdate(Long id, UserUpdatePerfilRequestDTO dto) {
        User perfil = userRepository.findById(id)
                .orElseThrow(() -> new UserDoesNotExist(id));

        if (dto.name() != null)
            perfil.setName(dto.name());
        if (dto.bio() != null)
            perfil.setBio(dto.bio());
        if (dto.whoCanComment() != null)
            perfil.setWhoCanComment(dto.whoCanComment());
        if (dto.favoriteAlbum() != null)
            perfil.setFavoriteAlbum(dto.favoriteAlbum());
        if (dto.favoriteArtist() != null)
            perfil.setFavoriteArtist(dto.favoriteArtist());
        if (dto.favoriteMusic() != null)
            perfil.setFavoriteArtist(dto.favoriteArtist());

        return userRepository.save(perfil);
    }

    @Transactional
    public User findUserById(Long id) {
        return this.userRepository.findFullById(id).orElseThrow(
                () -> new ResourceNotFoundException("Esse Usuario não foi encontrado"));
    }

    @Transactional
    public void followUser(Long myId, Long friendId) {
        if (myId.equals(friendId))
            throw new FollowYourself();

        User me = userRepository.findFullById(myId)
                .orElseThrow(() -> new UserDoesNotExist(myId));

        User friend = userRepository.findFullById(friendId)
                .orElseThrow(() -> new FriendDoesNotExist());

        if (!me.getFollowing().contains(friend)) {
            me.getFollowing().add(friend);

            userRepository.save(me);
        }
    }

    @Transactional
    public void unfollowUser(Long myId, Long friendId) {
        if (myId.equals(friendId))
            throw new FollowYourself();

        User me = userRepository.findFullById(myId)
                .orElseThrow(() -> new UserDoesNotExist(myId));

        User friend = userRepository.findFullById(friendId)
                .orElseThrow(
                        () -> new FriendDoesNotExist());

        if (me.getFollowing().contains(friend)) {
            me.getFollowing().remove(friend);

            userRepository.save(me);
        }
    }

    @Transactional
    public List<User> getFollowers(Long userId) {
        User user = findUserById(userId);
        return user.getFollowers().stream().collect(Collectors.toList());
    }

    @Transactional
    public List<User> getFollowing(Long userId) {
        User user = findUserById(userId);
        return user.getFollowing().stream().collect(Collectors.toList());
    }


//    @Transactional
//    public User findOrCreateUser(FirebaseToken decodedToken) {
//        return userRepository.findByIdLoginApi(decodedToken.getUid())
//                .orElseGet(() -> {
//                    UserRegisterRequestDTO dto = new UserRegisterRequestDTO(decodedToken.getName(),
//                            decodedToken.getUid(), Role.MEMBER, Privacy.PUBLIC, "", decodedToken.getPicture());
//                    return register(dto);
//
//                });
//    }

    @Transactional
    public User findOrCreateUser(FirebaseToken decodedToken) {
        return userRepository.findByIdLoginApi(decodedToken.getUid())
                .orElseGet(() -> {
                    String safeName = resolveDisplayName(decodedToken);

                    UserRegisterRequestDTO dto = new UserRegisterRequestDTO(
                            safeName,
                            decodedToken.getUid(),
                            Role.MEMBER,
                            Privacy.PUBLIC,
                            "", // Bio vazia
                            decodedToken.getPicture()
                    );

                    return register(dto);
                });
    }

    // Método Privado para Tratar casos em que decodedToken.getName() retorna null.
    private String resolveDisplayName(FirebaseToken token) {
        // Plano A: Nome da rede social
        String name = token.getName();
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }

        // Plano B: Prefixo do E-mail
        String email = token.getEmail();
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@")); // Pega apenas a parte antes do @
        }

        // Plano C: Fallback absoluto com UID
        String uid = token.getUid();
        return "User_" + uid.substring(0, Math.min(6, uid.length()));
    }

    @Transactional
    protected void applyPunishment(User target, Punishment punishment, Long daysOfSuspension) {
        switch (punishment) {
            case WARNING:
                break;
            case TEMPORARY_SUSPENSION:
                Long days = 7L;// padrão 7 dias
                target.setModerationStatus(ModerationStatus.SUSPENDED);
                if (daysOfSuspension != null)
                    days = daysOfSuspension;
                target.setSuspensionEndDate(LocalDateTime.now().plusDays(days));
                break;
            case ACCOUNT_DELETION:
                target.setModerationStatus(ModerationStatus.BANNED);
                break;
        }
    }

    @Transactional
    public void deleteAccount(Long id) {
        User target = userRepository.findById(id)
                .orElseThrow(() -> new UserDoesNotExist(id));
        userRepository.delete(target);
    }

    // NOVOS MÉTODOS DE CONTAGEM (Pura performance)
    @Transactional(readOnly = true)
    public Long countFollowers(Long userId) {
        return userRepository.countFollowersByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Long countFollowing(Long userId) {
        return userRepository.countFollowingByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Long countMediaLists(Long authorId) {
        return mediaListRepository.countByAuthorId(authorId);
    }

//    @Transactional
//    public boolean isFollowing(Long followerId, Long followedId) {
//        User follower = userRepository.findFullById(followerId).orElseThrow(() -> new UserDoesNotExist(followerId));
//        return follower.getFollowing()
//                .stream()
//                .anyMatch(user -> user.getId().equals(followedId));
//    }

    // REFATORAÇÃO: Otimizando o isFollowing para não explodir a memória
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followedId) {
        // Em vez de carregar a lista toda, delegamos a checagem para o banco
        return userRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }

}
