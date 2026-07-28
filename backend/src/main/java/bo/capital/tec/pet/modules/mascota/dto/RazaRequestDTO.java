package bo.capital.tec.pet.modules.mascota.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RazaRequestDTO {
    @NotBlank @Size(max = 100)
    private String nombre;
    @NotNull
    private Long especieId;
}
