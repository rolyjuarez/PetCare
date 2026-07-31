package bo.capital.tec.pet.modules.usuario.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UsuarioRol {
    private Long id;
    private Long usuarioId;
    private Long rolId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
