package bo.capital.tec.pet.modules.sucursal.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Sucursal {
    private Long id;
    private String nombre;
    private Long direccionId;
    private String telefono;
    private String email;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private Boolean activa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
