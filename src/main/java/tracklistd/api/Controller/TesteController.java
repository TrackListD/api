package tracklistd.api.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.FirebaseApp;

@RestController
@RequestMapping("/test")
public class TesteController {
    @GetMapping("/firebase")
    public String firebase() {
        return FirebaseApp.getInstance().getName();
    }
}
