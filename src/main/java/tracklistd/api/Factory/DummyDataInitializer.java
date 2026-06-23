package tracklistd.api.Factory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Repository.UserRepository;

@Component
@Profile("populate")
public class DummyDataInitializer implements CommandLineRunner {
    private final DatabaseSeederFactory factory;
    private final UserRepository userRepository;

    public DummyDataInitializer(DatabaseSeederFactory factory,
            tracklistd.api.Repository.UserRepository userRepository) {
        this.factory = factory;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Populando banco de dados com dados de teste...");

        User joao = factory.createAndSaveUser("João", Role.MEMBER);
        User maria = factory.createAndSaveUser("Maria", Role.MEMBER);
        joao.getFollowing().add(maria);
        userRepository.save(joao);

        Music musica = factory.createAndSaveMusic("Filme teste (Música)", "123");

        factory.createAndSaveRating(maria, musica, 5f, "Muito bom mesmo!");

        System.out.println("Banco de dados populado com sucesso!");
    }

}
