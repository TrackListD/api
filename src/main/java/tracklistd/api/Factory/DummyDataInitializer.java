package tracklistd.api.Factory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.ListType;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Repository.UserRepository;

import java.util.Set;

@Component
@Profile("populate")
public class DummyDataInitializer implements CommandLineRunner {
    private final DatabaseSeederFactory factory;
    private final UserRepository userRepository;

    public DummyDataInitializer(DatabaseSeederFactory factory, UserRepository userRepository) {
        this.factory = factory;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Iniciando a super povoação do banco de dados para testes...");

        // --- 1. CRIANDO OS USUÁRIOS ---
        User joao = factory.createAndSaveUser("Joao_Silva", Role.MEMBER);
        User maria = factory.createAndSaveUser("Maria_Music", Role.MEMBER);
        User carlos = factory.createAndSaveUser("Carlos_Rock", Role.MEMBER);
        User ana = factory.createAndSaveUser("Ana_Pop", Role.MEMBER);
        User admin = factory.createAndSaveUser("Admin_Tracklistd", Role.ADMIN);

        // Criando rede de relacionamentos (Seguidores/Seguindo)
        joao.getFollowing().add(maria);
        joao.getFollowing().add(carlos);
        maria.getFollowing().add(ana);
        carlos.getFollowing().add(joao);
        ana.getFollowing().add(maria);

        userRepository.save(joao);
        userRepository.save(maria);
        userRepository.save(carlos);
        userRepository.save(ana);

        // --- 2. CRIANDO O CATÁLOGO DE MÚSICAS ---
        Music m1 = factory.createAndSaveMusic("Blinding Lights", "0VjIjW4GlUZ7gBRg5766Of");
        Music m2 = factory.createAndSaveMusic("Bohemian Rhapsody", "7tFvjv629ag2Ty694w06Iq");
        Music m3 = factory.createAndSaveMusic("As It Was", "4D7wZ4DevX3v7m93Y6SV6u");
        Music m4 = factory.createAndSaveMusic("Starboy", "7MXV79g8ZpMOW9v7VT6XvY");
        Music m5 = factory.createAndSaveMusic("Hotel California", "40gJZvXwIoxp38B8Q76k67");
        Music m6 = factory.createAndSaveMusic("Billie Jean", "5v9Wn8vG8ZpMOW9v7VT6XvY");
        Music m7 = factory.createAndSaveMusic("Flowers", "0y60v9Wn8vG8ZpMOW9v7VT");

        // --- 3. CRIANDO AVALIAÇÕES (RATINGS) ---
        // Maria avaliando Pop clássico/moderno
        factory.createAndSaveRating(maria, m1, 5.0f,
                "Uma das melhores produções pop da década! O clima synthwave é viciante.");
        factory.createAndSaveRating(maria, m3, 4.5f, "Música super gostosa de ouvir no talo limpando a casa.");

        // Carlos, o Roqueiro
        factory.createAndSaveRating(carlos, m2, 5.0f,
                "Uma obra-prima absoluta. A seção de ópera mistura genialidade com loucura.");
        factory.createAndSaveRating(carlos, m5, 4.0f,
                "Solo de guitarra histórico, embora a rádio já tenha saturado um pouco.");

        // João sendo eclético
        factory.createAndSaveRating(joao, m4, 4.5f,
                "A batida do Daft Punk com o vocal do The Weeknd ficou absurda de boa.");
        factory.createAndSaveRating(joao, m6, 5.0f, "A linha de baixo mais icônica da história da música mundial.");

        // Ana descobrindo faixas
        factory.createAndSaveRating(ana, m7, 3.5f, "É um bom hino de superação, mas acho que enjoa um pouco rápido.");

        // --- 4. CRIANDO LISTAS DE MÍDIAS (MEDIA_LISTS) ---
        // Playlist pública e favorita do João
        factory.createAndSaveMediaList(
                joao,
                ListType.MUSIC,
                "Músicas para Treinar Pesado",
                Privacy.PUBLIC,
                true,
                "Playlist focada em treinos de alta intensidade, com muito metal, rock pesado e batidas rápidas.",
                null,
                Set.of("Treino", "Rock", "Academia"),
                m1, m4, m6);

        // Playlist de clássicos do Carlos
        factory.createAndSaveMediaList(
                carlos,
                ListType.MUSIC,
                "Ouro do Rock 70s/80s",
                Privacy.PUBLIC,
                false,
                "Uma coletânea contendo os maiores clássicos do rock das décadas de 70 e 80.",
                null,
                Set.of("Classicos", "Rock", "80s"),
                m2, m5);

        // Playlist focada da Ana
        factory.createAndSaveMediaList(
                ana,
                ListType.MUSIC,
                "Foco Profundo & Estudo",
                Privacy.PUBLIC,
                false,
                "Músicas calmas, lo-fi e instrumentais ideais para concentração e leitura.",
                null,
                Set.of("Lofi", "Foco", "Estudo"),
                m3, m7);

        // Playlist privada da Maria (Apenas de teste interno)
        factory.createAndSaveMediaList(
                maria,
                ListType.MUSIC,
                "Minhas madrugadas reflexivas",
                Privacy.PRIVATE,
                true,
                "Músicas nostálgicas e reflexivas para ouvir na calada da noite.",
                null,
                Set.of("Madrugada", "Sad", "Nostalgia"),
                m1, m3, m5);

        System.out.println("✅ Banco de dados populado com uma quantidade massiva de cenários de teste!");
    }
}