package bo.capital.tec.pet.mascota.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MascotaSummaryDTO {
    private Long id;
    private String nombre;
    private String especieNombre;
    private String razaNombre;
    private BigDecimal peso;
}
