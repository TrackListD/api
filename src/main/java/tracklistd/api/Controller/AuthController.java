package tracklistd.api.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.firebase.auth.FirebaseToken;
import tracklistd.api.Dto.User.UserRegisterResponseDTO;
import tracklistd.api.Entity.User;
import tracklistd.api.Service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserRegisterResponseDTO> login(@AuthenticationPrincipal FirebaseToken token) {
        User user = authService.loginOrRegister(token);

        UserRegisterResponseDTO response = new UserRegisterResponseDTO(user);

        return ResponseEntity.ok(response);
    }
}