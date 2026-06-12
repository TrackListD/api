package tracklistd.api.Integration.FirebaseAuth.Config;

import java.io.FileInputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseSetUp() {
        try {
            FileInputStream serviceAccount = new FileInputStream(
                    "src/main/resources/firebase/tracklistd-53c90-firebase-adminsdk-fbsvc-5a6b9ee13e.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return FirebaseApp.getInstance();
    }

}
