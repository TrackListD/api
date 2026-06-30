package tracklistd.api.Service;

import jakarta.transaction.Transactional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

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
import tracklistd.api.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    @Transactional
    public User findOrCreateUser(FirebaseToken decodedToken) {
        return userRepository.findByIdLoginApi(decodedToken.getUid())
                .orElseGet(() -> {
                    UserRegisterRequestDTO dto = new UserRegisterRequestDTO(decodedToken.getName(),
                            decodedToken.getUid(), Role.MEMBER, Privacy.PUBLIC, "", decodedToken.getPicture());
                    return register(dto);
                });
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

    @Transactional
    public boolean isFollowing(Long followerId, Long followedId) {
        User follower = userRepository.findFullById(followerId).orElseThrow(() -> new UserDoesNotExist(followerId));
        return follower.getFollowing()
                .stream()
                .anyMatch(user -> user.getId().equals(followedId));
    }

}
