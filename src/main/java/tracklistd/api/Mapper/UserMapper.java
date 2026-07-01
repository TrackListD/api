package tracklistd.api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tracklistd.api.Dto.Artist.ArtistMinDTO;
import tracklistd.api.Dto.Media.MediaMinDTO;
import tracklistd.api.Dto.User.UserMinResponseDTO;
import tracklistd.api.Dto.User.UserPerfilResponseDTO;
import tracklistd.api.Dto.User.UserRegisterResponseDTO;
import tracklistd.api.Entity.Artist;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserRegisterResponseDTO toRegisterDto(User user);

    @Mapping(target = "followersCount", source = "followersCount")
    @Mapping(target = "followingCount", source = "followingCount")
    @Mapping(target = "mediaListsCount", source = "mediaListsCount")
    @Mapping(target = "estaAtivo", expression = "java(user.getModerationStatus() == tracklistd.api.Entity.Enums.ModerationStatus.ACTIVE)")
    @Mapping(target = "currentUserIsFollowing", expression = "java(currentUserIsFollowing)")
    UserPerfilResponseDTO toPerfilDto(
            User user,
            Boolean currentUserIsFollowing,
            Long followersCount,
            Long followingCount,
            Long mediaListsCount);

    UserMinResponseDTO toMinDto(User user);

    default MediaMinDTO mapMediaToMinDto(Media media) {
        if (media == null) {
            return null;
        }
        return new MediaMinDTO(media);
    }

    default ArtistMinDTO mapArtistToMinDto(Artist artist) {
        if (artist == null) {
            return null;
        }
        return new ArtistMinDTO(artist);
    }
}