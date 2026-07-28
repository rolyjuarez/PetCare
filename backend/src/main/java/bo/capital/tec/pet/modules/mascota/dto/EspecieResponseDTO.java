package bo.capital.tec.pet.modules.mascota.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EspecieResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime createdAt;
}
