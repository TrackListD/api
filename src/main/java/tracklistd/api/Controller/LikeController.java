package tracklistd.api.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tracklistd.api.Dto.Like.LikeResponseDTO;
import tracklistd.api.Dto.User.UserMinResponseDTO;
import tracklistd.api.Entity.User;
import tracklistd.api.Service.LikeService;

@RestController
@RequestMapping("/publicationActions")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("{id}/like")
    public ResponseEntity<LikeResponseDTO> toggleLike(@PathVariable Long id, @AuthenticationPrincipal User user) {
        LikeResponseDTO response = likeService.toggleLike(user, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}/likes")
    public ResponseEntity<List<UserMinResponseDTO>> seeWhoLiked(@PathVariable Long id) {
        List<UserMinResponseDTO> usersWhoLiked = likeService.getWhoLiked(id);
        return ResponseEntity.ok(usersWhoLiked);
    }

}
