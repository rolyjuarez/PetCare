package bo.capital.tec.pet.servicio.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ServicioRequestDTO {
    @NotBlank @Size(max = 100)
    private String nombre;
    @Size(max = 500)
    private String descripcion;
    @NotNull @Min(15)
    private Integer duracionMinutos;
    @NotNull @DecimalMin("0.01")
    private BigDecimal precioBase;
    private String imagenUrl;
    private Boolean activo;
    @NotBlank
    private String categoria;
}
