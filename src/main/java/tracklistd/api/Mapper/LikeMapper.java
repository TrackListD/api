package tracklistd.api.Mapper;

import org.mapstruct.Mapper;
import tracklistd.api.Dto.User.UserMinResponseDTO;
import tracklistd.api.Entity.User;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    // referencias ao user para pegar quem curtiu a publicação de forma segura
    UserMinResponseDTO toUserMinDTO(User user);

    List<UserMinResponseDTO> toUserMinDTOList(List<User> users);
}