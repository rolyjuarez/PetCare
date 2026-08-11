package bo.capital.tec.pet.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClienteRequestDTO {
    @NotNull
    private Long personaId;
    private Long usuarioId;
    private String notas;
}
