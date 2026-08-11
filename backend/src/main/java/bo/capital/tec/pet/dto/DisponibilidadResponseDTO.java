package bo.capital.tec.pet.dto;

import lombok.*;
import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
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
