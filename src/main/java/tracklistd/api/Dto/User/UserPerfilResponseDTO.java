package tracklistd.api.Dto.User;

import tracklistd.api.Dto.Artist.ArtistMinDTO;
import tracklistd.api.Dto.Media.MediaMinDTO;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Enums.Role;

import java.time.LocalDate;

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
        ArtistMinDTO favoriteArtist,
        boolean currentUserIsFollowing) {
}