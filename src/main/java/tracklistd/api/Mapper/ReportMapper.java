package tracklistd.api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tracklistd.api.Dto.Report.ReportResponseDto;
import tracklistd.api.Entity.Report;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(target = "informerId", source = "userInformer.id")
    @Mapping(target = "userTargetId", source = "userTarget.id")
    ReportResponseDto toDto(Report report);
}