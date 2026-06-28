package tracklistd.api.Dto.MediaList;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tracklistd.api.Entity.Enums.Privacy;

import java.util.Set;

public final class MediaListEditRequestDto {

    private MediaListEditRequestDto() {}

    @Schema(description = "DTO para edição da review de uma lista de mídias")
    public record EditDescriptionRequestDto(
            @Size(max = 1000, message = "A descrição não pode passar de 1000 caracteres")
            @Schema(description = "Nova review para a lista", example = "Melhores álbuns de rock!")
            String newDescription
    ) {}

    @Schema(description = "DTO para renomear uma lista de mídias")
    public record EditNameRequestDto(
            @NotBlank(message = "O novo nome não pode estar em branco")
            @Size(max = 100, message = "O nome da lista não pode passar de 100 caracteres")
            @Schema(description = "Novo nome para a lista", example = "Álbuns favoritos atualizado")
            String newName
    ) {}

    @Schema(description = "DTO para alteração da privacidade de uma lista de mídias")
    public record EditPrivacyRequestDto(
            @NotNull(message = "A nova privacidade deve ser informada")
            @Schema(description = "Nova privacidade da lista", example = "PUBLIC", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"PUBLIC", "JUST_FOLLOWERS", "PRIVATE"})
            Privacy newPrivacy
    ) {}

    @Schema(description = "DTO para alteração da imagem de capa de uma lista de mídias")
    public record EditCoverImageRequestDto(
            @Size(max = 255, message = "A URL da imagem de capa deve ter no máximo 255 caracteres")
            @Schema(description = "Nova URL de capa para a lista")
            String newCoverImageUrl
    ) {}

    @Schema(description = "DTO para atualização completa das tags de uma lista")
    public record EditTagsRequestDto(
            @Size(max = 5, message = "A lista pode ter no máximo 5 tags")
            @Schema(description = "Novo conjunto de tags que substituirá as antigas", example = "[\"Rock\", \"Anos 90\"]")
            Set<String> newTags
    ) {}
}
