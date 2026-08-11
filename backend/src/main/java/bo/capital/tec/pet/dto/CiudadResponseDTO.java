package bo.capital.tec.pet.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CiudadResponseDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private BigDecimal latitud;
    private BigDecimal longitud;
}
