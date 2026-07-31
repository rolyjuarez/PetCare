package bo.capital.tec.pet.modules.menu.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Menu {
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private String url;
    private Integer orden;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
