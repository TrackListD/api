package tracklistd.api.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.firebase.auth.FirebaseToken;
import tracklistd.api.Dto.User.UserRegisterResponseDTO;
import tracklistd.api.Entity.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public AuthController() {
    }

    @PostMapping("/login")
    public ResponseEntity<UserRegisterResponseDTO> login(@AuthenticationPrincipal User user) {

        UserRegisterResponseDTO response = new UserRegisterResponseDTO(user);
        return ResponseEntity.ok(response);
    }
}