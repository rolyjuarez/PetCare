package bo.capital.tec.pet.modules.direccion.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CiudadRequestDTO {
    @NotBlank @Size(max = 100)
    private String nombre;
    @NotBlank @Size(max = 10)
    private String codigo;
    private BigDecimal latitud;
    private BigDecimal longitud;
}
