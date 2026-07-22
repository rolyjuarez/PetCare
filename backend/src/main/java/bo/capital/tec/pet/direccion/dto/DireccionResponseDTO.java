package bo.capital.tec.pet.direccion.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DireccionResponseDTO {
    private Long id;
    private String calle;
    private String numero;
    private String piso;
    private String apartamento;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String referencia;
    private Long ciudadId;
    private String ciudadNombre;
    private Long estadoId;
    private String estadoNombre;
}
