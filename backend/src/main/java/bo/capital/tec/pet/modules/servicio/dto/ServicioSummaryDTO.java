package bo.capital.tec.pet.modules.servicio.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ServicioSummaryDTO {
    private Long id;
    private String nombre;
    private BigDecimal precioBase;
    private String categoria;
}
