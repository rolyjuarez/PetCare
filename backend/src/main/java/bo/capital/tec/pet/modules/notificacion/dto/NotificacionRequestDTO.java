package bo.capital.tec.pet.modules.notificacion.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificacionRequestDTO {
    @NotNull
    private Long usuarioId;
    @NotBlank @Size(max = 200)
    private String titulo;
    @NotBlank @Size(max = 1000)
    private String mensaje;
    @Size(max = 50)
    private String tipo;
}
