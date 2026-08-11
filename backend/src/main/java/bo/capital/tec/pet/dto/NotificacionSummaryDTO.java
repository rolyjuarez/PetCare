package bo.capital.tec.pet.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificacionSummaryDTO {
    private Long id;
    private Long usuarioId;
    private String titulo;
    private String tipo;
    private Boolean leida;
    private LocalDateTime createdAt;
}
