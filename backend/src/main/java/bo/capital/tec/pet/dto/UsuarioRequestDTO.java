package bo.capital.tec.pet.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UsuarioRequestDTO {
    @NotBlank @Size(min = 4, max = 50)
    private String username;
    @NotBlank @Size(min = 6, max = 100)
    private String password;
    @NotNull
    private Long personaId;
}
