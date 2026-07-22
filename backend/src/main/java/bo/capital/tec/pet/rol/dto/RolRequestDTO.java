package bo.capital.tec.pet.rol.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RolRequestDTO {
    @NotBlank @Size(max = 50)
    private String nombre;
    @Size(max = 200)
    private String descripcion;
    private Boolean activo;
}
