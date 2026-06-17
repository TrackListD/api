package tracklistd.api.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tracklistd.api.Dto.Feed.PublicationFeedDTO;

import tracklistd.api.Entity.User;
import tracklistd.api.Service.FeedService;

@RestController
@RequestMapping("/api/feed")
public class FeedController {
    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<PublicationFeedDTO>> socialFeed(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(feedService.getSocialFeed(user.getId()));
    }

    @GetMapping("/global")
    public ResponseEntity<List<PublicationFeedDTO>> globalFeed(@AuthenticationPrincipal User user) {
        Long userId = (user != null) ? user.getId() : null;
        return ResponseEntity.ok(feedService.getGlobalFeed(userId));
    }

}
