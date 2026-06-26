package tracklistd.api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tracklistd.api.Dto.User.UserMinResponseDTO;
import tracklistd.api.Dto.User.UserPerfilResponseDTO;
import tracklistd.api.Dto.User.UserRegisterResponseDTO;
import tracklistd.api.Entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserRegisterResponseDTO toRegisterDto(User user);

    @Mapping(target = "followersCount", expression = "java((long) user.getFollowers().size())")
    @Mapping(target = "followingCount", expression = "java((long) user.getFollowing().size())")
    @Mapping(target = "estaAtivo", expression = "java(user.getModerationStatus() == tracklistd.api.Entity.Enums.ModerationStatus.ACTIVE)")
    UserPerfilResponseDTO toPerfilDto(User user);

    UserMinResponseDTO toMinDto(User user);
}