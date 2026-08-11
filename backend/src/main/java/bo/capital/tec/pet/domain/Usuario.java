package bo.capital.tec.pet.domain;

import lombok.*;
import bo.capital.tec.pet.domain.Persona;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {
    private Long id;
    private String username;
    private String password;
    private Long personaId;
    private Boolean activo;
    private LocalDateTime ultimoAcceso;
    private Integer intentosFallidos;
    private Boolean bloqueado;
    private String tokenRefresh;
    private Persona persona;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
