package bo.capital.tec.pet.vacuna.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VacunaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer periodicidadMeses;
    private LocalDateTime createdAt;
}
