package bo.capital.tec.pet.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RazaResponseDTO {
    private Long id;
    private String nombre;
    private Long especieId;
    private String especieNombre;
    private LocalDateTime createdAt;
}
