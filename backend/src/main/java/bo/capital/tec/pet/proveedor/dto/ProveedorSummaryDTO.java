package bo.capital.tec.pet.proveedor.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProveedorSummaryDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal calificacion;
    private Boolean verificado;
}
