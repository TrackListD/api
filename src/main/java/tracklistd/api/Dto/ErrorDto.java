package tracklistd.api.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "DTO padrão para representação de erros da API")
public record ErrorDto(
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        @Schema(description = "Data e hora do erro", example = "18-06-2026 22:47:20")
        LocalDateTime timestamp,

        @Schema(description = "Código do status HTTP", example = "400")
        Integer codeError,

        @Schema(description = "Descrição detalhada do status HTTP", example = "BAD_REQUEST")
        String status,

        @Schema(description = "Lista contendo as mensagens detalhadas dos erros", example = "[\"O comentário deve ter um texto\"]")
        List<String> errors
) {
}
