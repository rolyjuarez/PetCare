package bo.capital.tec.pet.modules.proveedorservicio.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProveedorServicioModalidad {
    private Long id;
    private Long proveedorServicioId;
    private String modalidad;
    private BigDecimal costoAdicional;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
