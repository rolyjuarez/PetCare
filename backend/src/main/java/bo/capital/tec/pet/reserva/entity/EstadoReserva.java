package bo.capital.tec.pet.reserva.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EstadoReserva {
    private Long id;
    private String nombre;
    private String descripcion;
    private String color;
    private String icono;
    private Integer orden;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
