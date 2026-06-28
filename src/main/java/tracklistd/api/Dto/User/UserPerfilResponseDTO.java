package tracklistd.api.Dto.User;

import tracklistd.api.Dto.Media.MediaMinDTO;
import tracklistd.api.Dto.MediaList.MediaListResponseDto;
import tracklistd.api.Dto.Rating.RatingResponseDto;
import tracklistd.api.Entity.Album;
import tracklistd.api.Entity.Artist;
import tracklistd.api.Entity.Music;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;
import java.time.LocalDate;
import java.util.List;

public record UserPerfilResponseDTO(
                Long id,
                String name,
                String profilePic,
                String bio,
                Role role,
                Privacy whoCanComment,
                LocalDate creationDate,
                boolean estaAtivo,
                Long followersCount,
                Long followingCount,
                Long mediaListsCount,
                MediaMinDTO favoriteAlbum,
                MediaMinDTO favoriteMusic,
                Artist favoriteArtist,
                boolean currentUserIsFollowing) {
}
