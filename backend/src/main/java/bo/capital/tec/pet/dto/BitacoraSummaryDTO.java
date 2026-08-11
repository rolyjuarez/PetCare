package bo.capital.tec.pet.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BitacoraSummaryDTO {
    private Long id;
    private String usuarioNombre;
    private String accion;
    private String entidad;
    private Long entidadId;
    private String ipAddress;
    private LocalDateTime createdAt;
}
