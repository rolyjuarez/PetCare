package bo.capital.tec.pet.modules.soporte.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
