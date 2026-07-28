package bo.capital.tec.pet.modules.usuario.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private Long personaId;
    private String personaNombre;
    private Boolean activo;
    private Boolean bloqueado;
    private LocalDateTime ultimoAcceso;
    private List<String> roles;
    private LocalDateTime createdAt;
}
