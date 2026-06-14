package tracklistd.api.Service;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.firebase.auth.FirebaseToken;
import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User loginOrRegister(FirebaseToken token) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token do Firebase não fornecido ou inválido");
        }
        String uid = token.getUid();

        User user = userRepository.findByIdLoginApi(uid);

        if (user == null) {
            String name = token.getName() != null ? token.getName() : "Usuário do Firebase";

            User newUser = new User(name, uid, Role.MEMBER, Privacy.PUBLIC);
            return userRepository.save(newUser);
        }

        return user;
    }
}