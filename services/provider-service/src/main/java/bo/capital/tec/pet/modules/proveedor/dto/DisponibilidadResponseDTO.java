package bo.capital.tec.pet.modules.proveedor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadResponseDTO {
    private Long id;
    private Long proveedorId;
    private Long servicioId;
    private String servicioNombre;
    private Integer diaSemana;
    private String diaSemanaNombre;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean activo;
}
