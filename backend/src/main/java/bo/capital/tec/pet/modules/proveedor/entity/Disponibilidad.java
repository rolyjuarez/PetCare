package bo.capital.tec.pet.modules.proveedor.entity;

import lombok.*;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Disponibilidad {
    private Long id;
    private Long proveedorId;
    private Long servicioId;
    private Integer diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
