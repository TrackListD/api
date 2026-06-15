package tracklistd.api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tracklistd.api.Dto.Rating.RatingOwnerResponseDto;
import tracklistd.api.Dto.Rating.RatingRequestDto;
import tracklistd.api.Dto.Rating.RatingResponseDto;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    //Target campo do DTO, source campo da Entidade
    //Transforma o DTO de request em entidade
    @Mapping(source = "media", target = "target")
    Rating toEntity(RatingRequestDto ratingRequestDto, Media media, User author);

    //Transforma a Entidade em DTO de response
    @Mapping(source = "target.name", target = "targetName")
    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "commentCount", target = "commentCount")
    @Mapping(source = "likeCount", target = "likeCount")
    RatingResponseDto toResponseDto(Rating rating, Integer commentCount, Long likeCount);

    //Transforma a Entidade em DTO de response "privado" do criador
    @Mapping(source = "ratingResponseDto", target = "publicDto")
    RatingOwnerResponseDto toOwnerResponseDTO(Rating rating, RatingResponseDto ratingResponseDto);


}
