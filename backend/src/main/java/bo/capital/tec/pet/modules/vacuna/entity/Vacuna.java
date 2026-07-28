package bo.capital.tec.pet.modules.vacuna.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Vacuna {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer periodicidadMeses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
