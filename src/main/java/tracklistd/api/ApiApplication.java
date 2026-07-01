package tracklistd.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling // Permite scheduling para poder requisitar um novo token da api do spotify de
					// 50 em 50 minutos
public class ApiApplication {
	public static void main(String[] args) {
		// Inicia a leitura do arquivo .env
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(ApiApplication.class, args);
	}
}