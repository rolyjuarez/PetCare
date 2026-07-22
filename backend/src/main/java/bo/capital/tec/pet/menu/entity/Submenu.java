package bo.capital.tec.pet.menu.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Submenu {
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private String url;
    private Integer orden;
    private Long menuId;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
