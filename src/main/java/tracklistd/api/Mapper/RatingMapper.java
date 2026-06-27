package tracklistd.api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tracklistd.api.Dto.Media.MediaMinDTO;
import tracklistd.api.Dto.Rating.RatingOwnerResponseDto;
import tracklistd.api.Dto.Rating.RatingRequestDto;
import tracklistd.api.Dto.Rating.RatingResponseDto;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Rating;
import tracklistd.api.Entity.User;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    //Transforma a Entidade em DTO de response
    @Mapping(source = "rating.author.id", target = "authorId")
    @Mapping(source = "rating.targetMedia", target = "targetMedia")
    @Mapping(source = "commentCount", target = "commentCount")
    @Mapping(source = "likeCount",    target = "likeCount")
    RatingResponseDto toResponseDto(Rating rating, Integer commentCount, Long likeCount);

    //Transforma a Entidade em DTO de response "privado" do criador
    @Mapping(source = "ratingResponseDto", target = "publicData")
    @Mapping(source = "rating.updateAt", target = "updatedAt") // Resolve o unmapped "updatedAt"
    @Mapping(source = "rating.whoCanSee", target = "whoCanSee") // Resolve o unmapped "whoCanSee"
    RatingOwnerResponseDto toOwnerResponseDTO(Rating rating, RatingResponseDto ratingResponseDto);


    default MediaMinDTO mapMediaToMinDto(Media media) {
        if (media == null) return null;
        return new MediaMinDTO(media);
    }

}
