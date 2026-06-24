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

    UserPerfilResponseDTO toPerfilDto(User user);

    UserMinResponseDTO toMinDto(User user);
}