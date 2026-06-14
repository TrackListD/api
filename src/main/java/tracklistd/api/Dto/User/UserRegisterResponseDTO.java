package tracklistd.api.Dto.User;

import tracklistd.api.Entity.User;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import tracklistd.api.Entity.Enums.ModerationStatus;

import java.time.LocalDateTime;

public record UserRegisterResponseDTO(
        Long id,
        String name,
        Role role,
        Privacy whoCanComment,
        String bio,
        ModerationStatus moderationStatus,
        LocalDateTime creationDate) {

    public UserRegisterResponseDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getRole(),
                user.getWhoCanComment(),
                user.getBio(),
                user.getModerationStatus(),
                user.getCreationDate());
    }
}