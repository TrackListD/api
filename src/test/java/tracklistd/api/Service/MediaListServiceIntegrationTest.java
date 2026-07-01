package tracklistd.api.Service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;
import tracklistd.api.Entity.Enums.ListType;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.User;
import tracklistd.api.Repository.MediaListRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
class MediaListServiceIntegrationTest {

    @Autowired
    private MediaListService mediaListService;

    @Autowired
    private MediaListRepository mediaListRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void createMediaList_whenSaved_shouldBePersistedAndRetrievedByAuthorAndListName() {
        // Arrange (Configuração)
        // 1. Criar e persistir o autor (User)
        User author = new User("Jane Doe", "jane_unique_login", Role.MEMBER, Privacy.PUBLIC);
        entityManager.persist(author);

        entityManager.flush();

        ListType typeOfList = ListType.MUSIC;
        String listName = "Minha Playlist de Integração";
        Privacy privacy = Privacy.PUBLIC;
        Boolean isFavorite = true;

        // Act (Ação)
        MediaList createdList = mediaListService.createMediaList(author, typeOfList, listName, privacy, isFavorite, null, null, null);
        assertNotNull(createdList);
        assertNotNull(createdList.getId());

        entityManager.flush();
        entityManager.clear(); // Limpa o contexto para forçar a consulta real ao banco

        // Assert (Validação)
        Optional<MediaList> foundListOpt = mediaListRepository.findMediaListByAuthorAndListName(author, listName);
        assertTrue(foundListOpt.isPresent());
        MediaList foundList = foundListOpt.get();

        assertEquals(createdList.getId(), foundList.getId());
        assertEquals(author.getId(), foundList.getAuthorPublication().getId());
        assertEquals(typeOfList, foundList.getTypeOfList());
        assertEquals(listName, foundList.getListName());
        assertEquals(privacy, foundList.getWhoCanSee());
        assertEquals(isFavorite, foundList.getIsFavorite());
    }
}
