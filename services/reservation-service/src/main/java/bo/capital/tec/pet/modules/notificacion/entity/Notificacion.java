package bo.capital.tec.pet.modules.notificacion.entity;

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
public class Notificacion {
    private Long id;
    private Long usuarioId;
    private String titulo;
    private String mensaje;
    private String tipo;
    private Boolean leida;
    private LocalDateTime fechaLectura;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
