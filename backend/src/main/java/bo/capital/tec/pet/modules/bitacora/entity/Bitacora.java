package bo.capital.tec.pet.modules.bitacora.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Bitacora {
    private Long id;
    private Long usuarioId;
    private String accion;
    private String entidad;
    private Long entidadId;
    private String datosAnteriores;
    private String datosNuevos;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
}
