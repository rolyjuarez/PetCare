package bo.capital.tec.pet.modules.proveedor.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProveedorEspecialidad {
    private Long id;
    private Long proveedorId;
    private Long servicioId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
