package tracklistd.api.Service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tracklistd.api.Dto.Feed.PublicationFeedDTO;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Mapper.FeedMapper;
import tracklistd.api.Repository.PublicationRepository;
import tracklistd.api.Repository.UserRepository;
import tracklistd.api.Repository.LikeRepository;
import tracklistd.api.Repository.CommentRepository;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    @InjectMocks
    private FeedService feedService;

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FeedMapper feedMapper;

    @Mock
    private UserService userService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Test
    void shouldReturnAllPrivaciesWhenUserRequestsOwnFeed() {
        Long userId = 1L;
        Long myUserId = 1L;

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Privacy>> captor = ArgumentCaptor.forClass(List.class);

        when(publicationRepository.findUserFeed(eq(userId), anyList()))
                .thenReturn(Collections.emptyList());

        List<PublicationFeedDTO> result = feedService.getUserFeed(userId, myUserId);

        assertNotNull(result);
        verify(publicationRepository).findUserFeed(eq(userId), captor.capture());
        
        List<Privacy> capturedPrivacies = captor.getValue();
        org.junit.jupiter.api.Assertions.assertTrue(capturedPrivacies.contains(Privacy.PUBLIC));
        org.junit.jupiter.api.Assertions.assertTrue(capturedPrivacies.contains(Privacy.JUST_FOLLOWERS));
        org.junit.jupiter.api.Assertions.assertTrue(capturedPrivacies.contains(Privacy.PRIVATE));
        org.junit.jupiter.api.Assertions.assertEquals(3, capturedPrivacies.size());
    }

    @Test
    void shouldReturnPublicAndFollowersWhenUserIsFollower() {
        Long userId = 2L;
        Long myUserId = 1L;

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Privacy>> captor = ArgumentCaptor.forClass(List.class);

        when(userService.isFollowing(myUserId, userId)).thenReturn(true);
        when(publicationRepository.findUserFeed(eq(userId), anyList()))
                .thenReturn(Collections.emptyList());

        List<PublicationFeedDTO> result = feedService.getUserFeed(userId, myUserId);

        assertNotNull(result);
        verify(publicationRepository).findUserFeed(eq(userId), captor.capture());

        List<Privacy> capturedPrivacies = captor.getValue();
        org.junit.jupiter.api.Assertions.assertTrue(capturedPrivacies.contains(Privacy.PUBLIC));
        org.junit.jupiter.api.Assertions.assertTrue(capturedPrivacies.contains(Privacy.JUST_FOLLOWERS));
        org.junit.jupiter.api.Assertions.assertFalse(capturedPrivacies.contains(Privacy.PRIVATE));
        org.junit.jupiter.api.Assertions.assertEquals(2, capturedPrivacies.size());
    }

    @Test
    void shouldReturnOnlyPublicWhenUserIsNotFollower() {
        Long userId = 2L;
        Long myUserId = 1L;

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Privacy>> captor = ArgumentCaptor.forClass(List.class);

        when(userService.isFollowing(myUserId, userId)).thenReturn(false);
        when(publicationRepository.findUserFeed(eq(userId), anyList()))
                .thenReturn(Collections.emptyList());

        List<PublicationFeedDTO> result = feedService.getUserFeed(userId, myUserId);

        assertNotNull(result);
        verify(publicationRepository).findUserFeed(eq(userId), captor.capture());

        List<Privacy> capturedPrivacies = captor.getValue();
        org.junit.jupiter.api.Assertions.assertTrue(capturedPrivacies.contains(Privacy.PUBLIC));
        org.junit.jupiter.api.Assertions.assertFalse(capturedPrivacies.contains(Privacy.JUST_FOLLOWERS));
        org.junit.jupiter.api.Assertions.assertFalse(capturedPrivacies.contains(Privacy.PRIVATE));
        org.junit.jupiter.api.Assertions.assertEquals(1, capturedPrivacies.size());
    }
}
