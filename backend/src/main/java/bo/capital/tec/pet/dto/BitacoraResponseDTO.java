package bo.capital.tec.pet.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BitacoraResponseDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String accion;
    private String entidad;
    private Long entidadId;
    private String datosAnteriores;
    private String datosNuevos;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
}
