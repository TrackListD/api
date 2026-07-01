package tracklistd.api.Service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Enums.ListType;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Entity.MediaList;
import tracklistd.api.Entity.User;
import tracklistd.api.Repository.CommentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void createComment_whenSaved_shouldBePersistedAndRetrievedByPost() {
        // Arrange
        // 1. Create and persist post author
        User postAuthor = new User("Jane Doe", "jane_unique_login", Role.MEMBER, Privacy.PUBLIC);
        entityManager.persist(postAuthor);

        // 2. Create and persist comment author
        User commentAuthor = new User("John Doe", "john_unique_login", Role.MEMBER, Privacy.PUBLIC);
        entityManager.persist(commentAuthor);

        // 3. Create and persist the post (MediaList)
        MediaList post = new MediaList(postAuthor, ListType.MUSIC, "My Music List", Privacy.PUBLIC, false, null, null, null);
        entityManager.persist(post);

        entityManager.flush();

        String commentText = "Great playlist!";

        // Act
        Comment createdComment = commentService.createComment(commentAuthor, post, commentText);
        assertNotNull(createdComment);
        assertNotNull(createdComment.getId());

        entityManager.flush();
        entityManager.clear(); // Clear entity manager context to force database retrieval

        // Assert
        List<Comment> foundComments = commentRepository.findAllByPost(post);
        assertEquals(1, foundComments.size());

        Comment foundComment = foundComments.get(0);
        assertEquals(createdComment.getId(), foundComment.getId());
        assertEquals(commentAuthor.getId(), foundComment.getAuthorPublication().getId());
        assertEquals(post.getId(), foundComment.getPost().getId());
        assertEquals(commentText, foundComment.getText());
    }
}
