package tracklistd.api.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ErrorDto(
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime timestamp,

        Integer codeError,

        String status,

        List<String> errors
) {
}
