package bo.capital.tec.pet.modules.notificacion.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificacionResponseDTO {
    private Long id;
    private String titulo;
    private String mensaje;
    private String tipo;
    private Boolean leida;
    private LocalDateTime fechaLectura;
    private LocalDateTime createdAt;
}
