package tracklistd.api.Service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import tracklistd.api.Dto.User.UserRegisterRequestDTO;
import tracklistd.api.Dto.User.UserUpdatePerfilRequestDTO;
import tracklistd.api.Entity.User;
import tracklistd.api.Repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(UserRegisterRequestDTO dto){
        if(userRepository.existsByIdLoginApi((dto.idLoginApi())))
            throw new RuntimeException("Usuário já está cadastrado no sistema!");

        User newUser = new User();
        newUser.setName(dto.name());
        newUser.setIdLoginApi(dto.idLoginApi());
        newUser.setRole(dto.role());
        newUser.setWhoCanComment(dto.whoCanComment());
        newUser.setBio(dto.bio());

        return userRepository.save(newUser);
    }

    @Transactional
    public User perfilUpdate(Long id, UserUpdatePerfilRequestDTO dto){
        User perfil = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));

        if(dto.name() != null)
            perfil.setName(dto.name());
        if(dto.bio() != null)
            perfil.setBio(dto.bio());
        if(dto.whoCanComment() != null)
            perfil.setWhoCanComment(dto.whoCanComment());

        return userRepository.save(perfil);
    }

    public void followUser(Long myId, Long friendId){
        if(myId.equals(friendId))
            throw new RuntimeException("Você não pode seguir você mesmo!");

        User me = userRepository.findById(myId)
                .orElseThrow(() -> new RuntimeException("Seu usuário não foi encontrado."));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Usuário que você deseja seguir não foi encontrado."));

        if(!me.getFollowing().contains(friend)){
            me.getFollowing().add(friend);

            userRepository.save(me);
        }
    }

    public void unfollowUser(Long myId, Long friendId){
        if(myId.equals(friendId))
            throw new RuntimeException("Você não pode deixar de seguir você mesmo!");

        User me = userRepository.findById(myId)
                .orElseThrow(() -> new RuntimeException("Seu usuário não foi encontrado."));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Usuário que você deseja deixar de seguir não foi encontrado."));

        if(me.getFollowing().contains(friend)){
            me.getFollowing().remove(friend);

            userRepository.save(me);
        }
    }
}
