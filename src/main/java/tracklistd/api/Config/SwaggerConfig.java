package tracklistd.api.Config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Backend TracklistD")
                        .version("v1.0")
                        .description("Documentação do nosso Backend")
                        .license(new License().name("Apache 2.0").url("https://apache.org"))
                        .contact(new Contact().name("TracklistD support API").email("tracklistd@gmail.com"))
                        );
    }
}
