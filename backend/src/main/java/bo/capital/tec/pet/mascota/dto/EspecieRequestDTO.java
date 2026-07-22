package bo.capital.tec.pet.mascota.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EspecieRequestDTO {
    @NotBlank @Size(max = 100)
    private String nombre;
    @Size(max = 255)
    private String descripcion;
}
