package bo.capital.tec.pet.modules.menu.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Permiso {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long rolId;
    private Long menuId;
    private Long submenuId;
    private Boolean crear;
    private Boolean leer;
    private Boolean actualizar;
    private Boolean eliminar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
