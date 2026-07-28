package bo.capital.tec.pet.modules.vacuna.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VacunaRequestDTO {
    @NotBlank @Size(max = 100)
    private String nombre;
    @Size(max = 500)
    private String descripcion;
    @NotNull @Min(1)
    private Integer periodicidadMeses;
}
