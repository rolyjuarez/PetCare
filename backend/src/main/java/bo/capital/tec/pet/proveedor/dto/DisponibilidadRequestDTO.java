package bo.capital.tec.pet.proveedor.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DisponibilidadRequestDTO {
    @NotNull
    private Long proveedorId;
    @NotNull
    private Long servicioId;
    @NotNull @Min(0) @Max(6)
    private Integer diaSemana;
    @NotNull
    private LocalTime horaInicio;
    @NotNull
    private LocalTime horaFin;
}
