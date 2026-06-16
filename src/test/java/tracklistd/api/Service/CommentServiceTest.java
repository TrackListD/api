package tracklistd.api.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tracklistd.api.Entity.Comment;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;
import tracklistd.api.Exceptions.CommentExceptions.CommentException;
import tracklistd.api.Exceptions.CommentExceptions.CommentOwershipViolation;
import tracklistd.api.Exceptions.CommentExceptions.CommentTextBlankException;
import tracklistd.api.Exceptions.CommentExceptions.SelfCommentException;
import tracklistd.api.Exceptions.CommentExceptions.CommentOnCommentException;
import tracklistd.api.Exceptions.ResourceNotFoundException;
import tracklistd.api.Repository.CommentRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private User author;
    private User postAuthor;
    private Rating post;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);

        postAuthor = new User();
        postAuthor.setId(2L);

        post = new Rating();
        ReflectionTestUtils.setField(post, "id", 100L);
        ReflectionTestUtils.setField(post, "author", postAuthor);
    }

    // --- createComment Tests ---

    @Test
    void createComment_whenTextIsBlank_shouldThrowCommentTextBlankException() {
        // Arrange
        String blankText = "   ";

        // Act & Assert
        assertThrows(CommentTextBlankException.class, () -> commentService.createComment(author, post, blankText));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenPostIsAComment_shouldThrowCommentException() {
        // Arrange
        Comment commentPost = new Comment();
        User differentAuthor = new User();
        differentAuthor.setId(3L);
        ReflectionTestUtils.setField(commentPost, "author", differentAuthor);

        // Act & Assert
        CommentOnCommentException exception = assertThrows(CommentOnCommentException.class,
                () -> commentService.createComment(author, commentPost, "My comment text"));
        assertEquals("Impossivel Comentar um comentario", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenAuthorIsOwnerOfPost_shouldThrowCommentException() {
        // Arrange
        ReflectionTestUtils.setField(post, "author", author);

        // Act & Assert

        SelfCommentException exception = assertThrows(SelfCommentException.class,
                () -> commentService.createComment(author, post, "My comment text"));
        assertEquals("Impossivel comentar no proprio Post", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenSuccess_shouldReturnCommentAndCallSave() {
        // Arrange
        String text = "Valid comment text";

        // Act
        Comment result = commentService.createComment(author, post, text);

        // Assert
        assertNotNull(result);
        assertEquals(author, result.getAuthorPublication());
        assertEquals(post, result.getPost());
        assertEquals(text, result.getText());

        verify(commentRepository, times(1)).save(result);
    }

    // --- editCommentText Tests ---

    @Test
    void editCommentText_whenCommentDoesNotExist_shouldThrowCommentException() {
        // Arrange
        Long commentId = 99L;
        String newText = "New text";
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.editCommentText(newText, commentId, author.getId()));
        assertEquals("Esse comentario não existe", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void editCommentText_whenUserIsNotOwner_shouldThrowCommentOwershipViolation() {
        // Arrange
        Long commentId = 10L;
        String newText = "New text";
        Comment comment = new Comment(author, post, "Old text");
        ReflectionTestUtils.setField(comment, "id", commentId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThrows(CommentOwershipViolation.class, () -> commentService.editCommentText(newText, commentId, 999L));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void editCommentText_whenNewTextIsBlank_shouldThrowCommentTextBlankException() {
        // Arrange
        Long commentId = 10L;
        String blankText = "   ";
        Comment comment = new Comment(author, post, "Old text");
        ReflectionTestUtils.setField(comment, "id", commentId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThrows(CommentTextBlankException.class,
                () -> commentService.editCommentText(blankText, commentId, author.getId()));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void editCommentText_whenSuccess_shouldUpdateTextAndCallSave() {
        // Arrange
        Long commentId = 10L;
        String newText = "Updated comment text";
        Comment comment = new Comment(author, post, "Old text");
        ReflectionTestUtils.setField(comment, "id", commentId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act
        commentService.editCommentText(newText, commentId, author.getId());

        // Assert
        assertEquals(newText, comment.getText());
        verify(commentRepository, times(1)).save(comment);
    }

    // --- deleteComment Tests ---

    @Test
    void deleteComment_whenCommentDoesNotExist_shouldThrowCommentException() {
        // Arrange
        Long commentId = 99L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.deleteComment(commentId, author.getId()));
        assertEquals("Esse comentario não existe", exception.getMessage());
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void deleteComment_whenUserIsNotOwner_shouldThrowCommentOwershipViolation() {
        // Arrange
        Long commentId = 10L;
        Comment comment = new Comment(author, post, "Comment text");
        ReflectionTestUtils.setField(comment, "id", commentId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThrows(CommentOwershipViolation.class, () -> commentService.deleteComment(commentId, 999L));
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void deleteComment_whenSuccess_shouldCallDelete() {
        // Arrange
        Long commentId = 10L;
        Comment comment = new Comment(author, post, "Comment text");
        ReflectionTestUtils.setField(comment, "id", commentId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act
        commentService.deleteComment(commentId, author.getId());

        // Assert
        verify(commentRepository, times(1)).delete(comment);
    }

    // --- getCommentsByUser Tests ---

    @Test
    void getCommentsByUser_whenSuccess_shouldReturnCommentsList() {
        // Arrange
        Comment comment = new Comment(author, post, "Comment text");
        List<Comment> expectedComments = Collections.singletonList(comment);
        when(commentRepository.getCommentsByAuthor(author)).thenReturn(expectedComments);

        // Act
        List<Comment> result = commentService.getCommentsByUser(author);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(comment, result.get(0));
        verify(commentRepository, times(1)).getCommentsByAuthor(author);
    }
}